package com.fragrancepluscustomerdatabase.scottauman;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 3/3/2016.
 */
public class MyCustomerRecyclerAdapter extends RecyclerView.Adapter<MyCustomerRecyclerAdapter.ViewHolder> {

    private List<Customer> customers;
    private static RecyclerViewClickListener itemListener;
    private int clicked_row;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        TextView first;
        TextView last;
        TextView email;
        TextView phone;
        CheckBox promotion;

        public ViewHolder(View v) {
            super(v);

           first = (TextView) v.findViewById(R.id.firstNameTextView);
           last = (TextView) v.findViewById(R.id.lastNameTextView);
            email = (TextView) v.findViewById(R.id.emailTextView);
            phone = (TextView) v.findViewById(R.id.phoneTextView);
            promotion = (CheckBox) v.findViewById(R.id.promotionCheckBox);

            v.setOnClickListener(this);

        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v,getAdapterPosition());
        }
    }

    public void addHeaderView(){

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyCustomerRecyclerAdapter(RecyclerViewClickListener itemListener) {
        customers = new ArrayList<>();
        this.itemListener = itemListener;
        clicked_row = 0;
    }

    public MyCustomerRecyclerAdapter(List<Customer> customers,View.OnClickListener onClickListener) {
        this.customers = customers;
        clicked_row = 0;
    }

    public void addCustomerToList(Customer customer){
        customers.add(customer);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyCustomerRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_recyclerview_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.first.setText(customers.get(position).getFirstName());
        holder.last.setText(customers.get(position).getLastName());
        holder.email.setText(customers.get(position).getEmail());
        holder.phone.setText(customers.get(position).getPhone());
        holder.promotion.setChecked(customers.get(position).isPromotion());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return customers.size();
    }



}
