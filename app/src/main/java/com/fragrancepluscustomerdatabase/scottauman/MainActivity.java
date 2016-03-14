package com.fragrancepluscustomerdatabase.scottauman;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BackToActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //sets up orientations for tablet and mobile
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            //set oreintation on all activiies for tablets
            Toast.makeText(this,"TABLET",Toast.LENGTH_LONG).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // setportraiot and rotation on all activites
            Toast.makeText(this,"PHONE",Toast.LENGTH_LONG).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }


        //runs code to check if first app launch
        //and makes sure files still exist
        new AppSetup(this);
    }

    @Override
    public void attachNewFragment(Fragment fragment) {

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.container,fragment).addToBackStack("this").commit();
    }

    @Override
    public void openNewActiviyt(Activity activity) {

    }


}
