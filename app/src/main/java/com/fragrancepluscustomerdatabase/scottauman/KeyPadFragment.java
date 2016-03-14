package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Scott on 3/6/2016.
 * shows a keybpad on device screen as a one step verification for applications
 *
 * NO USED IN THIS APPLICATION SCRAPED BY USER
 */
public class KeyPadFragment extends Fragment {

    private TextView codeTextView;
    private int numbers;
    private CoordinatorLayout passwordCoordLayout;

    public KeyPadFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.keypad_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        codeTextView = (TextView) view.findViewById(R.id.passwordTextView);
        passwordCoordLayout = (CoordinatorLayout) view.findViewById(R.id.passwordCoordLayout);
    }

    public void clicker(View v){

        Log.i(getClass().getSimpleName(), v.getId() + " CLicked");

        switch(v.getId()){
            case R.id.buttonExit:
                getActivity().finish(); //exit application / current activity
                break;
            case R.id.button0:
                addToCodeTextView("0");
                break;
            case R.id.button1:
                addToCodeTextView("1");
                break;
            case R.id.button2:
                addToCodeTextView("2");
                break;
            case R.id.button3:
                addToCodeTextView("3");
                break;
            case R.id.button4:
                addToCodeTextView("4");
                break;
            case R.id.button5:
                addToCodeTextView("5");
                break;
            case R.id.button6:
                addToCodeTextView("6");
                break;
            case R.id.button7:
                addToCodeTextView("7");
                break;
            case R.id.button8:
                addToCodeTextView("8");
                break;
            case R.id.button9:
                addToCodeTextView("9");
                break;
            case R.id.buttonDeleteBack:
                deleteOneSpace();
                break;
        }

        if(codeTextView.length() == 4 && checkForValidCode()){
            playSuccessSound();

        }else if(codeTextView.length() == 4 && !checkForValidCode()){
            Snackbar.make(passwordCoordLayout,"Invalid, please try again",Snackbar.LENGTH_SHORT).show();
            clearPasswordTextView();
            playErrorSound();
        }
    }

    private void launchMainActivity(){

        PreferenceManager.getDefaultSharedPreferences
                (getActivity().getApplicationContext()).edit().putBoolean("PASSWORD", true).apply();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void clearPasswordTextView(){
        codeTextView.setText(""); //cleat extview contents
    }

    private void deleteOneSpace() {

        if (codeTextView.length() != 0) {
            codeTextView.setText(codeTextView.getText().toString().substring(0, codeTextView.length() - 1));
            //codeTextView.setText(codeTextView.getText().toString() + "-");
            numbers = numbers - 1;
        }
    }

    private void playErrorSound(){
        MediaPlayer mPlayer = MediaPlayer.create(getActivity(),R.raw.alert_asterisk_1);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
         mPlayer.start();
    }

    private void playSuccessSound(){

        MediaPlayer mPlayer = MediaPlayer.create(getActivity(),R.raw.alert_3);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                launchMainActivity();
            }
        });
        mPlayer.start();
    }

    private void addToCodeTextView(String number){
        if(codeTextView.length() != 4){
            numbers = numbers + 1;
            codeTextView.setText(codeTextView.getText().toString() + number);
        }
    }

    private boolean checkForValidCode(){
        if(codeTextView.getText().toString().equals(getActivity().getString(R.string.code))){
            return true;
        }

        return false;
    }
}
