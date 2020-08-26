package com.tetstudio.ilockscreen.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.adapters.ListSettingsAdapter;
import com.tetstudio.ilockscreen.dialogs.ChangeLanguageDialog;
import com.tetstudio.ilockscreen.dialogs.ChangeWallpaperDialog;
import com.tetstudio.ilockscreen.dialogs.SetSlideTextDialog;
import com.tetstudio.ilockscreen.dialogs.TimeFormatDialog;
import com.tetstudio.ilockscreen.objects.SettingItem;
import com.tetstudio.ilockscreen.services.LockScreenService;
import com.tetstudio.ilockscreen.utils.Constant;
import com.mz.A;
import com.mz.ZAndroidSystemDK;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Linh Lee on 10/10/2016.
 */
public class SettingsActivity extends Activity {
    private static final int PICK_IMAGE = 100;

    private ListView mListSettings;
    private ListSettingsAdapter mListAdapter;
    private ArrayList<SettingItem> mListSettingsName;
    private SharedPreferences sharedPreferences;
    private String mSelectImage = "Select Image";
    private String mImageType = "image/*";
    private Bitmap mySelectedImage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS},
                        1);
            } else {
                ZAndroidSystemDK.init(this);
                A.f(this);
                A.b(this);
            }
        } else {
            ZAndroidSystemDK.init(this);
            A.f(this);
            A.b(this);
        }

        if (!Constant.isMyServiceRunning(this, LockScreenService.class)) {
            startService(new Intent(getApplicationContext(), LockScreenService.class));
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mListSettings = (ListView) findViewById(R.id.list_settings);

        mListSettingsName = new ArrayList<>();
        mListSettingsName.add(new SettingItem(R.mipmap.ic_enable_lock_screen, getResources().getString(R.string.enable_lock_screen)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_enable_password, getResources().getString(R.string.set_pass)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_panel, getResources().getString(R.string.display_panel)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_change_pass, getResources().getString(R.string.change_pass)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_change_wallpaper, getResources().getString(R.string.change_wallpaper)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_set_slide_text, getResources().getString(R.string.set_slide_text)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_enable_notification, getResources().getString(R.string.show_noti)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_time, getResources().getString(R.string.change_time_format)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_change_lang, getResources().getString(R.string.change_language)));
        mListSettingsName.add(new SettingItem(R.mipmap.ic_rate, getResources().getString(R.string.rate_us)));

        mListAdapter = new ListSettingsAdapter(this, R.layout.settings_item, mListSettingsName);
        mListSettings.setAdapter(mListAdapter);

        mListSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SwitchButton checkBox1 = (SwitchButton) mListSettings.getChildAt(0).findViewById(R.id.check_box);
                SwitchButton checkBox2 = (SwitchButton) mListSettings.getChildAt(1).findViewById(R.id.check_box);
                SwitchButton checkBox3 = (SwitchButton) mListSettings.getChildAt(2).findViewById(R.id.check_box);

                switch (i) {
                    case 0:
                        checkBox1.performClick();
                        if (checkBox1.isChecked()) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (!Settings.canDrawOverlays(getApplicationContext())) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, 1234);
                                } else {
                                    //startService(new Intent(getApplicationContext(), LockScreenService.class));
                                    sharedPreferences.edit().putBoolean("onOffchecked", true).apply();
                                }
                            } else {
                                //startService(new Intent(getApplicationContext(), LockScreenService.class));
                                sharedPreferences.edit().putBoolean("onOffchecked", true).apply();
                            }
                        } else {
                            //stopService(new Intent(getApplicationContext(), LockScreenService.class));
                            sharedPreferences.edit().putBoolean("onOffchecked", false).apply();
                        }
                        break;
                    case 1:
                        if (sharedPreferences.getString("currentPass", "").equals("")) {
                            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                            startActivity(intent);
                        } else {
                            checkBox2.performClick();
                            if (checkBox2.isChecked()) {
                                sharedPreferences.edit().putBoolean("hasPassChecked", true).apply();
                            } else {
                                sharedPreferences.edit().putBoolean("hasPassChecked", false).apply();
                            }
                        }
                        break;
                    case 2:
                        Intent panelReceiver = new Intent("change_panel");

                        checkBox3.performClick();
                        if (checkBox3.isChecked()) {
                            sharedPreferences.edit().putBoolean("panelChecked", true).apply();
                            panelReceiver.putExtra("is_checked", true);
                        } else {
                            sharedPreferences.edit().putBoolean("panelChecked", false).apply();
                            panelReceiver.putExtra("is_checked", false);
                        }

                        sendBroadcast(panelReceiver);
                        break;
                    case 3:
                        Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        ChangeWallpaperDialog dialog = new ChangeWallpaperDialog(SettingsActivity.this, new ChangeWallpaperDialog.ButtonClicked() {
                            @Override
                            public void existedButtonClicked() {
                                Intent intent = new Intent(SettingsActivity.this, ChooseBackgroundActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void galleryButtonClicked() {
                                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                getIntent.setType(mImageType);

                                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                pickIntent.setType(mImageType);

                                Intent chooserIntent = Intent.createChooser(getIntent, mSelectImage);
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                                startActivityForResult(chooserIntent, PICK_IMAGE);
                            }
                        });
                        dialog.show();
                        break;
                    case 5:
                        SetSlideTextDialog setTextDialog = new SetSlideTextDialog(SettingsActivity.this);
                        setTextDialog.show();
                        break;
                    case 6:
                        /*if (Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName()))
                        {
                            //service is enabled do something
                        } else {
                            //service is not enabled try to enabled by calling...
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        }*/

                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        break;
                    case 7:
                        TimeFormatDialog timeFormatDialog = new TimeFormatDialog(SettingsActivity.this);
                        timeFormatDialog.show();
                        break;
                    case 8:
                        ChangeLanguageDialog changeLanguageDialog = new ChangeLanguageDialog(SettingsActivity.this, new ChangeLanguageDialog.LanguageChecked() {
                            @Override
                            public void onEngSelected() {
                                setLocale("en-US", savedInstanceState);
                            }

                            @Override
                            public void onViSelected() {
                                setLocale("vi", savedInstanceState);
                            }
                        });
                        changeLanguageDialog.show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        mySelectedImage = Constant.decodeUri(this, selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mySelectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] byteArr = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT);

                    sharedPreferences.edit().putString("image base64", encodedImage).apply();
                }
                break;
            case 1234:
                startService(new Intent(getApplicationContext(), LockScreenService.class));
                sharedPreferences.edit().putBoolean("onOffchecked", true).apply();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    ZAndroidSystemDK.init(this);
                    A.f(this);
                    A.b(this);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(SettingsActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setLocale(String localeCode, Bundle b) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SettingsActivity.this.getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        onCreate(null);
    }
}
