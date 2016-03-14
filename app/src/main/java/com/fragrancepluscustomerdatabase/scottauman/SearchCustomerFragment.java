package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 3/3/2016.
 */
public class SearchCustomerFragment extends Fragment implements RecyclerViewClickListener, FragmentManager.OnBackStackChangedListener {

    private LoadCustomerIntoView loadCustomerIntoView;
    private RecyclerView mRecyclerView;
    private MyCustomerRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Customer> customerList = new ArrayList<>();
    private TextView noCustomersTextView;

    public SearchCustomerFragment(){

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        showBottomSheetMenu(v, position);
    }

    private void showBottomSheetMenu(View view, final int position){

        final TextView fName = (TextView) view.findViewById(R.id.firstNameTextView);
        final TextView lName = (TextView) view.findViewById(R.id.lastNameTextView);
        final TextView email = (TextView) view.findViewById(R.id.emailTextView);
        final TextView phone = (TextView) view.findViewById(R.id.phoneTextView);
        final CheckBox promo = (CheckBox) view.findViewById(R.id.promotionCheckBox);


        new BottomSheet.Builder(getActivity()).title("Customer Options").sheet(R.menu.list).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.add:
                       //add new customer launch add fragment over the top
                        launchAddNewFragment();
                        break;
                    case R.id.delete:
                        //delete customer from local file
                        //launch dialog fragment making sure user wants to complete this action
                        showConfirmDialog(buildCustomerObject(fName.getText().toString(),
                                lName.getText().toString(),email.getText().toString(),
                                phone.getText().toString(),promo.isChecked()),position);
                        break;
                    case R.id.edit:
                        //edit customer launch add fragment and input customer information into
                        //edittexts
                       launchEditNewFragment(buildCustomerObject(fName.getText().toString(),
                               lName.getText().toString(),email.getText().toString(),
                               phone.getText().toString(),promo.isChecked()));
                        break;
                }
            }
        }).show();
    }


    /**
     * builds customer objects given the customers information provided on the form
     * @return new Customer object
     */
    private Customer buildCustomerObject(String f,String l,String e, String p,boolean promo){

        Customer customer = new Customer(f,l,e,p,promo);

        Log.i(getClass().getSimpleName(), "EDIT ON CUSTOMER " + customer.getFirstName() + " " +
                customer.getLastName());
        return customer;
    }

    private void launchAddNewFragment(){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
        FragmentTransaction trans = manager.beginTransaction();
        trans.add(R.id.container, new AddCustomerFragment()).addToBackStack("add").commit();
    }

    private void launchEditNewFragment(Customer customer){

        EditCustomerFragment editCustomerFragment = new EditCustomerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("CUSTOMER", customer);
        editCustomerFragment.setArguments(bundle);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
        FragmentTransaction trans = manager.beginTransaction();
        trans.add(R.id.container,editCustomerFragment).addToBackStack("edit").commit();
    }

    public void showConfirmDialog(Customer customer, final int pos){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set title
        alertDialogBuilder.setTitle("Confirm Delete?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to delete this customer? \n" +
                        "Details: \t " + customer.getFirstName() + " " + customer.getLastName() + " " +
                        customer.getEmail() + " " + customer.getPhone())
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity

                        //delete customer from local file
                        try {
                            new FileHelper(getActivity()).createUpdatedFile(new InputStreamConverter(getActivity()).deleteSpecificCustomer(
                                    new FileInputStream(new FileHelper().getFile_location()),pos));

                            if(loadCustomerIntoView != null && loadCustomerIntoView.getStatus() == AsyncTask.Status.RUNNING){
                                loadCustomerIntoView.cancel(true);
                            }

                            loadCustomerIntoView = new LoadCustomerIntoView();
                            loadCustomerIntoView.execute(getActivity());

                            playSound();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_customer_fragment,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hides toolbar from specific fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noCustomersTextView = (TextView) view.findViewById(R.id.noCustomersTextView);
        noCustomersTextView.setVisibility(View.INVISIBLE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyCustomerRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        loadCustomerIntoView = new LoadCustomerIntoView();
        loadCustomerIntoView.execute(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(loadCustomerIntoView != null && loadCustomerIntoView.getStatus() == AsyncTask.Status.RUNNING){
            loadCustomerIntoView.cancel(true);
            Log.i(getClass().getSimpleName(),"Cancelled AsyncTask");
        }
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        Log.i(getClass().getSimpleName(), "back stack changed ");
        int backCount = 0;


        try {
            backCount = getActivity().getSupportFragmentManager().getBackStackEntryCount();
            Log.i(getClass().getSimpleName(), "CURRENT BACK STACK COUNT" + backCount);
        }catch(NullPointerException npe){
            npe.printStackTrace();
        }

        if (backCount == 1){
            if(loadCustomerIntoView != null && loadCustomerIntoView.getStatus() == AsyncTask.Status.RUNNING){
                loadCustomerIntoView.cancel(true);
            }

            loadCustomerIntoView = new LoadCustomerIntoView();
            loadCustomerIntoView.execute(getActivity());
        }
    }

    class LoadCustomerIntoView extends AsyncTask<Context,Customer,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(customerList.size() == 0){
                //no customers in file
                noCustomersTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAdapter = new MyCustomerRecyclerAdapter(SearchCustomerFragment.this);
            mRecyclerView.setAdapter(mAdapter);
            customerList.clear();
        }

        @Override
        protected void onProgressUpdate(Customer... values) {
            super.onProgressUpdate(values);

            mAdapter.addCustomerToList(values[0]);
            mAdapter.notifyItemInserted(mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();

            Log.i(getClass().getSimpleName(),values[0].getFirstName());
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
        protected Void doInBackground(Context... params) {

            try {
                new InputStreamConverter(getActivity()).convertInputStreamToString
                        (new FileInputStream(new FileHelper().getFile_location()), new PopToList() {
                    @Override
                    public void addNewCustomerToList(Customer customer) {
                        customerList.add(customer);
                        publishProgress(customer);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(getClass().getSimpleName(), ";BACK TO SEARCH FRAGMNET");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName()," ON RESUME");
    }
}
