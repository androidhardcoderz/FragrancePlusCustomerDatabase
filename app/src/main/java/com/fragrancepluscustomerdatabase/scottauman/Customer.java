package com.fragrancepluscustomerdatabase.scottauman;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Scott on 3/3/2016.
 */
public class Customer implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean promotion;

    public Customer(){

    }

    @Override public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Customer))return false;
        Customer otherMyClass = (Customer)other;

        return (firstName.equals(otherMyClass.firstName) &&
                lastName.equals(otherMyClass.lastName) && email.equals(otherMyClass.getEmail()) &&
                phone.equals(otherMyClass.getPhone()) && promotion == otherMyClass.isPromotion());
    }

    /**
     * Returns an integer hash code for this object. By contract, any two
     * objects for which {@link #equals} returns {@code true} must return
     * the same hash code value. This means that subclasses of {@code Object}
     * usually override both methods or neither method.
     * <p/>
     * <p>Note that hash values must not change over time unless information used in equals
     * comparisons also changes.
     * <p/>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_hashCode">Writing a correct
     * {@code hashCode} method</a>
     * if you intend implementing your own {@code hashCode} method.
     *
     * @return this object's hash code.
     * @see #equals
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Customer(String firstName,String lastName,String email,String phone,boolean promotion){

        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPhone(phone);
        setPromotion(promotion);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    protected Customer(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phone = in.readString();
        promotion = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeByte((byte) (promotion ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
