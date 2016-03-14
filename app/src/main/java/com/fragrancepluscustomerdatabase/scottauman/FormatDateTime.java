package com.fragrancepluscustomerdatabase.scottauman;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Scott on 3/3/2016.
 */
public class FormatDateTime {

    /**
     * @param date string date provided by dropbox file metadata
     * @return formatted date string in MM/dd/yyyy format with time and timezone
     */
    public String formatDropBoxDate(String date) {

        SimpleDateFormat parseDropBoxDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        SimpleDateFormat dateDropBoxFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss z");

        try {
            Date d = parseDropBoxDate.parse(date);
            return dateDropBoxFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * @param date the curretn date object
     * @return string version of current date see: dateDropBoxFormat regex
     */
    public String formatLastUpdate(Date date){
        SimpleDateFormat parseDropBoxDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        SimpleDateFormat dateDropBoxFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss z");
        return dateDropBoxFormat.format(new Date());
    }


}
