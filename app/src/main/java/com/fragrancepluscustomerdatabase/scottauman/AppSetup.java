package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Scott on 3/3/2016.
 * creates files and variables for the application to render itself
 */
public class AppSetup {
    
    public AppSetup(Context context){

        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("FIRST",true) == true){
            //create file in documents
            new FileHelper().createFileInDocuments();
            if(new FileHelper().checkFile()){
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                        .putBoolean("FIRST",false).apply();
            }
        }else{
            new FileHelper().createFileInDocuments();
        }
    }
}
