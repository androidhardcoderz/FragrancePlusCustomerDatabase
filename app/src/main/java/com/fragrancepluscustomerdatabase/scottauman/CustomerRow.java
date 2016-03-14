package com.fragrancepluscustomerdatabase.scottauman;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Scott on 3/3/2016.
 */
public class CustomerRow extends LinearLayout {

    public CustomerRow(Context context,Customer customer) {
        super(context);

        LayoutInflater.from(getContext()).inflate(R.layout.customer_recyclerview_row,this,true);

        TextView first = (TextView) this.findViewById(R.id.firstNameTextView);
        TextView last = (TextView) this.findViewById(R.id.lastNameTextView);
        TextView email = (TextView) this.findViewById(R.id.emailTextView);
        TextView phone = (TextView) this.findViewById(R.id.phoneTextView);
        CheckBox promotion = (CheckBox) this.findViewById(R.id.promotionsCheckBox);

        first.setText(customer.getFirstName());
        last.setText(customer.getLastName());
        email.setText(customer.getEmail());
        phone.setText(customer.getPhone());
        promotion.setChecked(customer.isPromotion());

    }

    public CustomerRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomerRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
