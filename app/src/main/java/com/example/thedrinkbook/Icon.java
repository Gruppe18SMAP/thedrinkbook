package com.example.thedrinkbook;

import android.content.Context;
import android.widget.ImageView;

class Icon {

    // properties i ikon

    Context c;
    String url;
    ImageView imageview;

    public Icon(Context c, String url, ImageView iv){
        this.c = c;
        this.url = url;
        this.imageview = iv;
    }

}
