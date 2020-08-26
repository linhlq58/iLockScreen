package com.tetstudio.ilockscreen.objects;

/**
 * Created by Linh Lee on 10/21/2016.
 */
public class SettingItem {
    private int image;
    private String name;

    public SettingItem(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
