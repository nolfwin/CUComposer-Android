package com.example.user.cucomposer_android;

import android.graphics.Color;
import android.os.Environment;

/**
 * Created by Nuttapong on 2/1/2015.
 */
public class Config {
    public final static String appFolder = Environment.getExternalStorageDirectory()+"/PopTime";
    public final static String fullSongFolder = appFolder + "/Songs";

    public final static int inactiveColor = Color.argb(255,200,200,200);


}
