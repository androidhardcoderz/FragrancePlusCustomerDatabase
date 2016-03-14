package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Scott on 3/3/2016.
 */
public class DropBoxFragment extends Fragment {

    // In the class declaration section:
    private CoordinatorLayout coordinatorLayout;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private GetFileDetails getFileDetails;
    private DownloadFileAndReplace downloadFileAndReplace;
    private UploadFileToDropBox uploadFileToDropBox;
    private TextView lastUpload,filesAdded,fileName,fileSize,filePath,progressIndicator;
    private ImageView syncButton,dropBox,download;
    private RotateAnimation anim;
    AppKeyPair appKeys;


    public DropBoxFragment(){

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("FILECREATED?",false) == false){
            //create file on dropbox server
            if(uploadFileToDropBox != null && uploadFileToDropBox.getStatus() == AsyncTask.Status.RUNNING){
                uploadFileToDropBox.cancel(true);
            }

            //use a BG thread to store a empty file onto personal /app directory located on dropbox
            uploadFileToDropBox = new UploadFileToDropBox();
            uploadFileToDropBox.execute(getActivity());
        }

        //refresh textview contents run getDropBoxMetaData again
        if(getFileDetails != null && getFileDetails.getStatus() == AsyncTask.Status.RUNNING){
            getFileDetails.cancel(true);
        }

        getFileDetails = new GetFileDetails();
        getFileDetails.execute();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hides toolbar from specific fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        appKeys = new AppKeyPair(getActivity().getResources().getString(R.string.Dropbox_api_key)
                ,getActivity().getResources().getString(R.string.dropbox_secret_key));
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        if(DropBoxPreferences.getOathKey(getActivity()).equals("")){
            mDBApi.getSession().startOAuth2Authentication(getActivity());
        }else{
            mDBApi.getSession().setOAuth2AccessToken(DropBoxPreferences.getOathKey(getActivity()));
        }
    }

    private DropBoxMetaData getFileDetails() throws DropboxException {

        DropboxAPI.Entry existingEntry = mDBApi.metadata(DropBoxPreferences.getDropBoxFilePath(getActivity()), 1, null, false, null);

        DropBoxMetaData dropBoxMetaData = new DropBoxMetaData();
        dropBoxMetaData.setName(existingEntry.fileName());
        dropBoxMetaData.setLastUpdate(DropBoxPreferences.getLastSync(getActivity()));
        dropBoxMetaData.setSize(humanReadableByteCount(existingEntry.bytes, false));
        dropBoxMetaData.setFilePath(existingEntry.path);


        return dropBoxMetaData;
    }

    /**
     * @param bytes the number of bytes of the files
     * @param si
     * @return String representing the bytes converted to roman size
     * ex: 1000bytes = 1MB
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private void uploadNewFileToDropBox() throws FileNotFoundException, DropboxException {

        File file = new FileHelper().getFile_location();
        FileInputStream fis = new FileInputStream(file);

        DropboxAPI.Entry newEntry = mDBApi.putFileOverwrite(Strings.FILENAME, fis, file.length(), null);
        Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.path);

        DropBoxPreferences.setFilesAdded(getActivity(), 0);
        DropBoxPreferences.setDropBoxFilePath(getActivity(), newEntry.path);
        DropBoxPreferences.setLastSync(getActivity(),newEntry.clientMtime);

    }

    /**
     * Plays sound file from /raw folder
     * using MediaPlayer static class
     * class waits for loading to be completed of mp3 file
     * and released itself when sound completes
     *
     */
    private void playSound(){
        MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.desk_bell);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cordinatedLayout);
        lastUpload = (TextView) view.findViewById(R.id.lastUploadTextView);
        filesAdded = (TextView) view.findViewById(R.id.filesAddedTextView);
        fileName = (TextView) view.findViewById(R.id.dropBoxFileNameTextView);
        fileSize = (TextView) view.findViewById(R.id.fileSizeTextView);
        filePath = (TextView) view.findViewById(R.id.filePathTextView);
        progressIndicator = (TextView) view.findViewById(R.id.progressIndicatorTextView);


        dropBox = (ImageView) view.findViewById(R.id.dropBoxImageView);
        dropBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //get package manager to dropbox application
                    PackageManager manager = getActivity().getPackageManager();
                    Intent i = manager.getLaunchIntentForPackage("com.dropbox.android");
                    i.addCategory(Intent.CATEGORY_LAUNCHER);

                    //build listing of activites on device with that package name installed
                    List activities = manager.queryIntentActivities(i,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    boolean isIntentSafe = activities.size() > 0;

                    //if the intent is safe meaning an application with that package name exists
                    if (isIntentSafe) {
                        startActivity(i);
                    }
                }catch(NullPointerException npe){
                    npe.printStackTrace();
                    //if app not listed show snackbar message and ask if user wants to be
                    //directed to install the application
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "DropBox Application Not Found On Device", Snackbar.LENGTH_LONG)
                            .setAction("Download It", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String appPackageName = "com.dropbox.android"; // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            });

                    snackbar.show();
                }
            }
        });

        syncButton = (ImageView) view.findViewById(R.id.syncImageView);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refresh textview contents run getDropBoxMetaData again
                if(uploadFileToDropBox != null && uploadFileToDropBox.getStatus() == AsyncTask.Status.RUNNING){
                    uploadFileToDropBox.cancel(true);
                }

                //use a BG thread to store a empty file onto personal /app directory located on dropbox
                uploadFileToDropBox = new UploadFileToDropBox();
                uploadFileToDropBox.execute(getActivity());

            }
        });

        download =(ImageView) view.findViewById(R.id.downloadImageView);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download files from dropbox and replace local file with dropbox file
                showDialog("Please be advised you will be replacing the local " +
                        "file on the device with the file located in the DropBox server " +
                        "Are you sure you want to do this? If you replace the local file any new customers will be erased","Warning");

            }
        });

    }

    private void startSyncAnimation(ImageView iv){

        if(anim != null){
            anim.cancel();
        }
        anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(Animation.INFINITE);
        anim.setDuration(1000);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(anim);

    }

    private void stopSyncAnimation(){
        if(anim != null){
            anim.cancel();
        }
    }

    private void showSnackBarMessage(String message){

        Snackbar.make(coordinatorLayout,
                message,Snackbar.LENGTH_LONG).show();

    }

    private void showErrorSnackBarMessage(final String message){

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("View", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showErrorDialog(message, "Error");
                    }
                });

        snackbar.show();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dropbox_fragment,container,false);
    }

    private void showDialog(String message,String title){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity(),android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        if (downloadFileAndReplace != null && downloadFileAndReplace.getStatus() == AsyncTask.Status.RUNNING) {
                            downloadFileAndReplace.cancel(true);
                        }

                        downloadFileAndReplace = new DownloadFileAndReplace();
                        downloadFileAndReplace.execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

    private void showErrorDialog(String message,String title){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity(),android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                DropBoxPreferences.setOathKey(getActivity(),accessToken);
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    class UploadFileToDropBox extends AsyncTask<Context,Exception,Boolean>{

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(Context... params) {
            try {
                uploadNewFileToDropBox();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                publishProgress(e);
            } catch (DropboxException e) {
                e.printStackTrace();
                publishProgress(e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);

            if(aVoid){
                playSound();
                Snackbar.make(coordinatorLayout,"File Uploaded To DropBox",Snackbar.LENGTH_LONG).show();

                //refresh textview contents run getDropBoxMetaData again
                if(getFileDetails != null && getFileDetails.getStatus() == Status.RUNNING){
                    getFileDetails.cancel(true);
                }

                getFileDetails = new GetFileDetails();
                getFileDetails.execute();

                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("FILECREATED?",true).apply();
            }
        }

        @Override
        protected void onProgressUpdate(Exception... values) {
            super.onProgressUpdate(values);

            showErrorSnackBarMessage("Failed To Upload File To DropBox Check The Problem Listed: " +
                    values[0].getMessage().toString());
        }
    }


    class GetFileDetails extends  AsyncTask<Void,DropboxException,DropBoxMetaData>{

        @Override
        protected void onPostExecute(DropBoxMetaData aVoid) {
            super.onPostExecute(aVoid);

            showSnackBarMessage("File Details Are Being Updated");

            if(aVoid != null){
                //populate data with respective textviews
                setTextDetails(lastUpload,new FormatDateTime().formatDropBoxDate(DropBoxPreferences.getLastSync(getActivity())));
                setTextDetails(fileName, aVoid.getName());
                setTextDetails(fileSize,aVoid.getSize());
                setTextDetails(filePath, aVoid.getFilePath());
                setTextDetails(filesAdded, DropBoxPreferences.getFilesAdded(getActivity()) + " customers");
            }else{
                //populate data with respective textviews
                lastUpload.setText("NONE");
                fileName.setText("NONE");
                fileSize.setText("NONE");
                filePath.setText("NONE");
                filesAdded.setText("NONE");
            }

            stopSyncAnimation();
        }


        private void setTextDetails(TextView tv,String string){
            try{
                if(string == null)
                    throw new Exception();
                tv.setText(string);
            }catch(Exception ex){
                ex.printStackTrace();
                tv.setText("N/A");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startSyncAnimation(syncButton);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected DropBoxMetaData doInBackground(Void... params) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                return getFileDetails();
            } catch (DropboxException e) {
                e.printStackTrace();
               publishProgress(e);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(DropboxException... values) {
            super.onProgressUpdate(values);

            showErrorSnackBarMessage("Cannot Get File Details From Server Check Problem Listed: " + values[0].getMessage().toString());

        }
    }

    class DownloadFileAndReplace extends AsyncTask<Void,Exception,Boolean>{

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                showSnackBarMessage("File Downloaded To " + new FileHelper().getFile_location());
            }
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(new FileHelper().getFile_location());
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile(Strings.FILENAME, null, outputStream, null);
                Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                publishProgress(e);
            } catch (DropboxException e) {
                e.printStackTrace();
                publishProgress(e);
            }

            return false;
        }

        @Override
        protected void onProgressUpdate(Exception... values) {
            super.onProgressUpdate(values);
            showErrorSnackBarMessage("Could Not Download File See More Info: " + values[0].getMessage().toString());
        }
    }

    /**
     * downloads the file on DROPBOX and compares entries to the local file
     * if local file contains extra entries create temp file and add entries DROPBOX file
     *
     */
    class SyncTask extends AsyncTask<Void,String,Void>{

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
           showSnackBarMessage(values[0]);

        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {

            try {

                publishProgress("DOWNLOADING FILES");
                //get local list of customers
                List<Customer> locals = new ArrayList<Customer>
                        (new InputStreamConverter(getActivity()).getAllCustomers
                                ((new FileInputStream(new FileHelper().getFile_location()))));

                //get server side list of customers
                FileOutputStream outputStream = new FileOutputStream(new FileHelper().getTemp_file());
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile(Strings.FILENAME, null, outputStream, null);
                List<Customer> server = new ArrayList<Customer>
                        (new InputStreamConverter(getActivity()).getAllCustomers(new FileInputStream(new FileHelper().getTemp_file())));

                System.out.println(locals.size());
                System.out.println(server.size());

                publishProgress("COMPARING FILES");
                if(locals.size() > server.size()) {
                    Log.i(getClass().getSimpleName(), "LOCAL FILE CONTAINS MORE CUSTOMERS");
                    buildNewSyncedList(locals,server);
                }else{
                    Log.i(getClass().getSimpleName(), "FILES ARE SYNCED");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (DropboxException e) {
                e.printStackTrace();
            }

            return null;
        }

        private List<Customer> buildNewSyncedList(List<Customer> local, List<Customer> server){



            List<Customer> serverList = new CopyOnWriteArrayList<>();
            serverList.addAll(server);

                for (int i = 0; i < local.size(); i++) {
                    Customer customer = local.get(i);
                    if(!serverList.contains(customer)){
                        Log.i(getClass().getSimpleName(), "ADDING " + customer.getFirstName() + " " + customer.getLastName());
                        serverList.add(customer);
                    }
                }

            System.out.println(serverList.size());


            return server;
        }
    }
}
