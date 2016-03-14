package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Scott on 3/3/2016.
 */
public class DropBoxPreferences {

    private final static String DROPBOX_TOKEN_KEY = "dropboxtoken";
    private final static String DROPBOX_PATH = "dropboxpath";
    private final static String FILES_ADDED = "files_added";
    private final static String LAST_SYNC = "last_sync";

    public static void setOathKey(Context context,String token){
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putString(DROPBOX_TOKEN_KEY,token).apply();
    }

    public static String getOathKey(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(DROPBOX_TOKEN_KEY,"");
    }

    public static String getDropBoxFilePath(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(DROPBOX_PATH,"");
    }

    public static void setDropBoxFilePath(Context context,String path){
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putString(DROPBOX_PATH,path).apply();
    }

    public static int getFilesAdded(Context context){
        return PreferenceManager.getDefaultSharedPreferences
                (context.getApplicationContext()).getInt(FILES_ADDED, 0);
    }

    public static void setFilesAdded(Context context,int files){
        PreferenceManager.getDefaultSharedPreferences
                (context.getApplicationContext()).edit().putInt(FILES_ADDED, files).apply();
    }

    public static void addToNumberOfFilesAdded(Context context){
        int filesAdded = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt(FILES_ADDED,0);
        filesAdded += 1;
        setFilesAdded(context,filesAdded);
    }

    public static String getLastSync(Context context){
        return PreferenceManager.getDefaultSharedPreferences
                (context.getApplicationContext()).getString(LAST_SYNC,"");
    }

    public static void setLastSync(Context context,String path){
        PreferenceManager.getDefaultSharedPreferences
                (context.getApplicationContext()).edit().putString(LAST_SYNC,path).apply();
    }
}
