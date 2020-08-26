package com.tetstudio.ilockscreen.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.tetstudio.ilockscreen.R;
import com.tetstudio.ilockscreen.objects.NotificationObject;
import com.tetstudio.ilockscreen.utils.iOSTextView;
import com.tetstudio.ilockscreen.utils.iOSTextViewMedium;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 10/18/2016.
 */
public class ListNotificationAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<NotificationObject> listNotification;

    public ListNotificationAdapter(Context context, int layout, ArrayList<NotificationObject> listNotification) {
        this.context = context;
        this.layout = layout;
        this.listNotification = listNotification;
    }

    @Override
    public int getCount() {
        return listNotification.size();
    }

    @Override
    public Object getItem(int position) {
        return listNotification.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layout, parent, false);
        }

        ImageView notiIcon = (ImageView) convertView.findViewById(R.id.noti_icon);
        iOSTextViewMedium notiTitle = (iOSTextViewMedium) convertView.findViewById(R.id.noti_title);
        iOSTextView notiContent = (iOSTextView) convertView.findViewById(R.id.noti_content);

        notiTitle.setText(listNotification.get(position).getTitle());
        notiContent.setText(listNotification.get(position).getContent());

        if (listNotification.get(position).getIconBase64() != null) {
            byte[] imageAsBytes = Base64.decode(listNotification.get(position).getIconBase64(), Base64.DEFAULT);
            Bitmap myIcon = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            notiIcon.setImageBitmap(myIcon);
        }

        return convertView;
    }
}
