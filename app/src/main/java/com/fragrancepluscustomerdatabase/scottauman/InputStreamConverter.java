package com.fragrancepluscustomerdatabase.scottauman;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 11/23/2015.
 */
public class InputStreamConverter {

    Context context;

    public InputStreamConverter(Context context){
        this.context = context;
    }

    /*
       converts an input stream to a single string
       used for xml and json files
     */
    public String convertInputStreamToString(InputStream is)
            throws IOException {

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        return result;
    }

    /*
       converts an input stream into custom java objects
       includes a interface to call back when a java object is created
       calling class receives the object
     */
    public void convertInputStreamToString(InputStream is,PopToList popToList)
            throws IOException {

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));

        String line = "";

        while ((line = bufferedReader.readLine()) != null) {

            String[] split = line.split(Strings.COMMA);

            if(split.length == 0)
                continue;

            System.out.println(split[0] + " "+ split[1] + " " + split[2] + " " + split[3] + " "  + split[4]);
            Customer customer = new Customer();
            customer.setFirstName(split[0]);
            customer.setLastName(split[1]);
            customer.setEmail(split[2]);
            customer.setPhone(split[3]);

            if(split[4].equals("true")){
                customer.setPromotion(true);
            }else{
                customer.setPromotion(false);
            }

            popToList.addNewCustomerToList(customer);
        }

    }


    /*
       converts an input stream into custom java objects
       includes a interface to call back when a java object is created
       calling class receives the object
     */
    public List<Customer> getAllCustomers(InputStream is)
            throws IOException {

        List<Customer> customers = new ArrayList<Customer>();


        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));

        String line = "";

        while ((line = bufferedReader.readLine()) != null) {

            String[] split = line.split(Strings.COMMA);
            Customer customer = new Customer();
            customer.setFirstName(split[0]);
            customer.setLastName(split[1]);
            customer.setEmail(split[2]);
            customer.setPhone(split[3]);

            if(split[4].equals("true")){
                customer.setPromotion(true);
            }else{
                customer.setPromotion(false);
            }

            customers.add(customer);

        }

        return customers;

    }

    /*
      converts an input stream into custom java objects
      deletes the specific index of customer from the file
    */
    public List<Customer> deleteSpecificCustomer(InputStream is,int pos)
            throws IOException {

        List<Customer> customers = new ArrayList<Customer>();


        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(is));

        String line = "";

        while ((line = bufferedReader.readLine()) != null) {

            String[] split = line.split(Strings.COMMA);
            Customer customer = new Customer();
            customer.setFirstName(split[0]);
            customer.setLastName(split[1]);
            customer.setEmail(split[2]);
            customer.setPhone(split[3]);

            if(split[4].equals("true")){
                customer.setPromotion(true);
            }else{
                customer.setPromotion(false);
            }

            customers.add(customer);

        }

        customers.remove(pos);

        return customers;

    }









}
