package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Scott on 3/3/2016.
 */
public class FileHelper {

    private Context context;
    private File file_location;
    private File temp_file;

    public FileHelper(Context context){
        this.context = context;
        file_location = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), Strings.FILENAME);
        temp_file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), Strings.TEMP_FILE);
    }

    public FileHelper(){
        file_location = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), Strings.FILENAME);
        temp_file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), Strings.TEMP_FILE);
    }

    public boolean checkFile(){
        File root = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), Strings.FILENAME);
       return root.exists();
    }

    /**
     * creates an empty string file in internal memory on device
     * checks if file exists then creates if it does not exist
     */
    public void createFileInDocuments(){
        File root = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS), Strings.FILENAME);
        if(!root.exists()) {
            try {
                root.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void addNewCustomer(Customer customer){

        BufferedWriter bw = null;

        try {
            // APPEND MODE SET HERE
            bw = new BufferedWriter(new FileWriter(file_location, true));
            bw.write(getCustomerLine(customer));
            bw.newLine();
            bw.flush();

            Log.i(getClass().getSimpleName(),"CUSTOMER SAVED!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                // just ignore it
            }
        } // end try/catch/finally

    }

    /**
     * returns and builds a string in the containing order found in the txt file
     * @param customer
     * @return
     */
    private String getCustomerLine(Customer customer) {
        return customer.getFirstName() + Strings.COMMA + customer.getLastName()
                + Strings.COMMA + customer.getEmail()  + Strings.COMMA + customer.getPhone() + Strings.COMMA + String.valueOf(customer.isPromotion());
    }

    public void createUpdatedFile(List<Customer> customers){

        String fLine = "";
        for(Customer customer: customers){
            fLine += getCustomerLine(customer) + "\n";
        }

        BufferedWriter bw = null;

        if(fLine == null)
            return;

        try {
            // APPEND MODE SET HERE
            bw = new BufferedWriter(new FileWriter(file_location,false));
            bw.write(fLine);
            bw.flush();

            Log.i(getClass().getSimpleName(),"FILE CREATED AFTER DELETION/EDIT");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                // just ignore it
            }
        } // end try/catch/finally


    }

    public File getFile_location() {
        return file_location;
    }

    public void setFile_location(File file_location) {
        this.file_location = file_location;
    }

    public File getTemp_file() {
        return temp_file;
    }
}
