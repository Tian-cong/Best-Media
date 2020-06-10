package com.tiancong.bestwish.utils;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class FileUtils {

    public static boolean fileIsExists(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String setFileNameAndPath() {
        String mFileName = "v"
                + "_" + (DateFormat.format("HHmmss", Calendar.getInstance(Locale.CHINA))) + ".aac";
        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AAA/";
        File file = new File(mFilePath);
        if (!file.exists()) file.mkdirs();
        return mFilePath = mFilePath + mFileName;
    }

    public static String setBackPath() {
        String mFileName = "v"
                + "_back_" + (DateFormat.format("HHmmss", Calendar.getInstance(Locale.CHINA))) + ".aac";
        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/";
        File file = new File(mFilePath);
        if (!file.exists()) file.mkdirs();
        return mFilePath = mFilePath + mFileName;
    }
}
