package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class KeypadActivity extends AppCompatActivity {

    KeyPadFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keypad);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("PASSWORD",false) == true){
            startActivity(new Intent(this,MainActivity.class));
        }

        fragment = (KeyPadFragment) getSupportFragmentManager().findFragmentById(R.id.keypad_fragment);
    }

    public void myOnClick(View v){
        fragment.clicker(v);
    }

}
