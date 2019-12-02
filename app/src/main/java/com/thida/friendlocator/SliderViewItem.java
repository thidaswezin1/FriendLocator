package com.thida.friendlocator;

import android.graphics.Bitmap;

public class SliderViewItem {
    Bitmap image;
    String userName;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
