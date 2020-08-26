package com.tetstudio.ilockscreen.activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.adapters.GalleryAdapter;
import com.tetstudio.ilockscreen.dialogs.WallpaperOptionDialog;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Linh Lee on 10/12/2016.
 */
public class ChooseBackgroundActivity extends Activity implements View.OnClickListener {
    private Gallery gallery;
    private GalleryAdapter galleryAdapter;
    private ArrayList<Integer> imgId;
    private ImageView backgroundImage;
    private Button chooseButton;
    private Button cancelButton;
    private SharedPreferences sharedPreferences;
    private String imageBase64;
    private WallpaperManager wallpaperManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_background);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        wallpaperManager = WallpaperManager.getInstance(this);

        gallery = (Gallery) findViewById(R.id.bg_gallery);
        backgroundImage = (ImageView) findViewById(R.id.bg_img);
        chooseButton = (Button) findViewById(R.id.choose_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        imgId = new ArrayList<>();
        imgId.add(R.mipmap.wallpager_21);
        imgId.add(R.mipmap.wallpager_22);
        imgId.add(R.mipmap.wallpager_23);
        imgId.add(R.mipmap.wallpager_24);
        imgId.add(R.mipmap.wallpager_25);
        imgId.add(R.mipmap.wallpager_26);
        imgId.add(R.mipmap.wallpager_27);
        imgId.add(R.mipmap.wallpager_28);
        imgId.add(R.mipmap.wallpager_29);
        imgId.add(R.mipmap.wallpager_30);
        imgId.add(R.mipmap.wallpager_31);
        imgId.add(R.mipmap.wallpager_32);
        imgId.add(R.mipmap.wallpager_33);
        imgId.add(R.mipmap.wallpager_34);
        imgId.add(R.mipmap.wallpager_35);
        imgId.add(R.mipmap.wallpager_36);
        imgId.add(R.mipmap.wallpager_37);
        imgId.add(R.mipmap.wallpager_38);
        imgId.add(R.mipmap.wallpager_39);
        imgId.add(R.mipmap.wallpager_40);
        imgId.add(R.mipmap.wallpager_41);
        imgId.add(R.mipmap.wallpager_42);
        imgId.add(R.mipmap.wallpager_43);
        imgId.add(R.mipmap.wallpager_44);
        imgId.add(R.mipmap.wallpager_45);
        imgId.add(R.mipmap.wallpager_01);
        imgId.add(R.mipmap.wallpager_02);
        imgId.add(R.mipmap.wallpager_03);
        imgId.add(R.mipmap.wallpager_04);
        imgId.add(R.mipmap.wallpager_05);
        imgId.add(R.mipmap.wallpager_06);
        imgId.add(R.mipmap.wallpager_07);
        imgId.add(R.mipmap.wallpager_08);
        imgId.add(R.mipmap.wallpager_09);
        imgId.add(R.mipmap.wallpager_10);
        imgId.add(R.mipmap.wallpager_11);
        imgId.add(R.mipmap.wallpager_12);
        imgId.add(R.mipmap.wallpager_13);
        imgId.add(R.mipmap.wallpager_14);
        imgId.add(R.mipmap.wallpager_15);
        imgId.add(R.mipmap.wallpager_16);
        imgId.add(R.mipmap.wallpager_17);
        imgId.add(R.mipmap.wallpager_18);
        imgId.add(R.mipmap.wallpager_19);
        imgId.add(R.mipmap.wallpager_20);

        galleryAdapter = new GalleryAdapter(this, imgId);
        gallery.setAdapter(galleryAdapter);
        gallery.setSelection(4);

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                backgroundImage.setImageResource(imgId.get(i));
            }
        });

        chooseButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_button:
                WallpaperOptionDialog dialog = new WallpaperOptionDialog(ChooseBackgroundActivity.this, new WallpaperOptionDialog.ButtonClicked() {
                    @Override
                    public void homeButtonClicked() {
                        try {
                            wallpaperManager.setResource(imgId.get(gallery.getSelectedItemPosition()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }

                    @Override
                    public void lockButtonClicked() {
                        sharedPreferences.edit().putInt("background img", imgId.get(gallery.getSelectedItemPosition())).apply();
                        sharedPreferences.edit().putString("image base64", "").apply();
                        finish();
                    }

                    @Override
                    public void homeLockButtonClicked() {
                        try {
                            wallpaperManager.setResource(imgId.get(gallery.getSelectedItemPosition()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sharedPreferences.edit().putInt("background img", imgId.get(gallery.getSelectedItemPosition())).apply();
                        sharedPreferences.edit().putString("image base64", "").apply();
                        finish();
                    }
                });
                dialog.show();
                break;
            case R.id.cancel_button:
                finish();
                break;
        }
    }
}
