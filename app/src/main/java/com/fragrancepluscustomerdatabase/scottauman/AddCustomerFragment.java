package com.fragrancepluscustomerdatabase.scottauman;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.lang3.text.WordUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Scott on 3/3/2016.
 */
public class AddCustomerFragment extends Fragment {

    private EditText firstName,lastName,email,phone;
    private Button save;
    private CheckBox promotion;
    private CoordinatorLayout cordLayout;

    public AddCustomerFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hides toolbar from specific fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstName = (EditText) view.findViewById(R.id.firstNameEditText);
        lastName = (EditText) view.findViewById(R.id.lastNameEditText);
        email = (EditText) view.findViewById(R.id.emailEditText);
        phone = (EditText) view.findViewById(R.id.phoneEditText);
        save = (Button) view.findViewById(R.id.saveButton);
        promotion = (CheckBox) view.findViewById(R.id.promotionsCheckBox);
        cordLayout = (CoordinatorLayout) view.findViewById(R.id.cordLayout);

        //sets android filter mask on phone edit text
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //set text watcher mask on first and last name to capitalize the first letter
        setCapitalizeTextWatcher(firstName);
        setCapitalizeTextWatcher(lastName);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(getClass().getSimpleName(), "Save Button Clicked");

                //write customer to file
                if (doubleChecksBeforeSave()) {
                    addCustomerToFile();
                } else {
                    showOverideDialog();
                }

            }
        });

    }

    private int dimen(int mb_height_56) {

        return (int) getActivity().getResources().getDimension(mb_height_56);
    }

    private void addCustomerToFile() {

        new FileHelper().addNewCustomer(buildCustomerObject());
        playSound();
        showSnackBarMessage();
        showCompleteView();

        //adds the number of files added since last dropbox update
        DropBoxPreferences.addToNumberOfFilesAdded(getActivity());
    }

    private void showCompleteView() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reloadFragmentContents();
            }
        }, 1000);
    }

    /**
     * Plays sound file from /raw folder
     * using MediaPlayer static class
     * class waits for loading to be completed of mp3 file
     * and released itself when sound completes
     *
     */
    private void playSound(){
        MediaPlayer mp = MediaPlayer.create(getActivity(),R.raw.desk_bell);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
            }
        });
    }

    private void showSnackBarMessage(){
        Snackbar.make(cordLayout,"Customer Saved",Snackbar.LENGTH_LONG).show();
    }

    /**
     * clears all edittext views out to allow another customer
     * to place theit information in resets fragment state to default
     *
     */
    private void reloadFragmentContents(){

        firstName.setText("");
        lastName.setText("");
        email.setText("");
        phone.setText("");
        promotion.setChecked(false);

        firstName.requestFocus();
    }

    /**
     * builds customer objects given the customers information provided on the form
     * @return new Customer object
     */
    private Customer buildCustomerObject(){

        Customer customer = new Customer(firstName.getEditableText().toString()
                ,lastName.getEditableText().toString(),email.getEditableText()
                .toString(),phone.getEditableText().toString(),promotion.isChecked());
        return customer;
    }

    /**
     * allows the changing of the characters in the edittext capitalizes the first letter in the name
     * typed in
     * @param editText edittext to allow text to be changed
     */
    public static void setCapitalizeTextWatcher(final EditText editText) {
        final TextWatcher textWatcher = new TextWatcher() {

            int mStart = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStart = start + count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Use WordUtils.capitalizeFully if you only want the first letter of each word to be capitalized
                String capitalizedText = WordUtils.capitalize(editText.getText().toString());
                if (!capitalizedText.equals(editText.getText().toString())) {
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            editText.setSelection(mStart);
                            editText.removeTextChangedListener(this);
                        }
                    });
                    editText.setText(capitalizedText);
                }
            }
        };

        editText.addTextChangedListener(textWatcher);
    }


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_customer_fragment,container,false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * checks if any or all data has been entered in the provded form
     * if data is incompleted show dialogbox letting user know
     *
     */
    private boolean doubleChecksBeforeSave(){

        boolean state = true;

        if(firstName.getEditableText().toString().equals("")){
            state = false;
        }else if(lastName.getEditableText().toString().equals("")){
            state = false;
        }else if(email.getEditableText().toString().equals("") || !isEmailValid(email.getEditableText().toString())){
            state = false;
        }else if(!phone.getEditableText().toString().equals("") && phone.getEditableText().toString().length() != 14){
            state = false;
        }

        return state;
    }

    private boolean doubleChecksBeforeExit(){
        boolean state = true;
        if(!firstName.getEditableText().toString().equals("")){
            state = false;
        }else if(!lastName.getEditableText().toString().equals("")){
            state = false;
        }else if(!email.getEditableText().toString().equals("")){
            state = false;
        }else if(!phone.getEditableText().toString().equals("") && phone.getEditableText().toString().length() != 14){
            state = false;
        }



        return state;
    }

    public void showExitingDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set title
        alertDialogBuilder.setTitle("Exit Add Customer?");

        // set dialog message
        alertDialogBuilder
                .setMessage("You entered data for a customer " +
                        "are you sure you want to exit and cancel changes?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                })
                .setNegativeButton("Go Back",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }



    public void showOverideDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set title
        alertDialogBuilder.setTitle("Override?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Some Information Is Missing Proceed Anyway?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        addCustomerToFile();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
