package com.tetstudio.ilockscreen.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.objects.SettingItem;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 10/10/2016.
 */
public class ListSettingsAdapter extends BaseAdapter {
    private Activity context;
    private int layout;
    private ArrayList<SettingItem> listSettings;
    private SharedPreferences sharedPreferences;

    public ListSettingsAdapter(Activity context, int layout, ArrayList<SettingItem> listSettings) {
        this.context = context;
        this.layout = layout;
        this.listSettings = listSettings;
    }


    @Override
    public int getCount() {
        return listSettings.size();
    }

    @Override
    public Object getItem(int position) {
        return listSettings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(layout, parent, false);
        }

        ImageView settingsImage = (ImageView) convertView.findViewById(R.id.item_image);
        TextView settingsName = (TextView) convertView.findViewById(R.id.item_name);
        final SwitchButton checkBox = (SwitchButton) convertView.findViewById(R.id.check_box);

        settingsImage.setImageResource(listSettings.get(position).getImage());
        settingsName.setText(listSettings.get(position).getName());

        if (position == 0 || position == 1 || position == 2) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean onOffChecked = sharedPreferences.getBoolean("onOffchecked", false);
        boolean hasPassChecked = sharedPreferences.getBoolean("hasPassChecked", false);
        boolean panelChecked = sharedPreferences.getBoolean("panelChecked", false);

        switch (position) {
            case 0:
                if (onOffChecked) {
                    checkBox.setChecked(true);

                } else {
                    checkBox.setChecked(false);
                }
                break;
            case 1:
                if (hasPassChecked) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                break;
            case 2:
                if (panelChecked) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                break;
        }

        return convertView;
    }
}
