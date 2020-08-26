package com.tetstudio.ilockscreen.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Linh Lee on 10/17/2016.
 */
public class NotificationObject implements Parcelable {
    private int id;
    private String iconBase64;
    private String title;
    private String content;

    public NotificationObject(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public NotificationObject(int id, String iconBase64, String title, String content) {
        this.id = id;
        this.iconBase64 = iconBase64;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIconBase64() {
        return iconBase64;
    }

    public void setIconBase64(String iconBase64) {
        this.iconBase64 = iconBase64;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(iconBase64);
        dest.writeString(title);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<NotificationObject> CREATOR
            = new Parcelable.Creator<NotificationObject>() {
        public NotificationObject createFromParcel(Parcel in) {
            return new NotificationObject(in);
        }

        public NotificationObject[] newArray(int size) {
            return new NotificationObject[size];
        }
    };

    private NotificationObject(Parcel in) {
        id = in.readInt();
        iconBase64 = in.readString();
        title = in.readString();
        content = in.readString();
    }
}
