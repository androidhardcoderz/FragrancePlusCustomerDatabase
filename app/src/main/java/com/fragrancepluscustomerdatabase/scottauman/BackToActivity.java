package com.fragrancepluscustomerdatabase.scottauman;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Scott on 3/3/2016.
 */
public interface BackToActivity {

    void attachNewFragment(Fragment fragment);
    void openNewActiviyt(Activity activity);
}
