package hnweb.com.findchurches.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hnweb on 16-Aug-17.
 */

public class Utils_Dialog {

    public static class DialogsUtils {
        public static ProgressDialog showProgressDialog(Context context, String text){
            ProgressDialog m_Dialog = new ProgressDialog(context);
            m_Dialog.setMessage(text);
            m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_Dialog.setCancelable(false);
            m_Dialog.show();
            return m_Dialog;
        }

    }

    public static void myToast1(Context context, String message)
    {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }


   /* public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }*/


    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());

    }

    public static String getDate_in_mm_dd_yyyy( String string_date_from_server)
    {
        String ndate;
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
        fromFormat.setLenient(false);
        //DateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
        toFormat.setLenient(false);

        Date date = null;
        try {
            date = fromFormat.parse(string_date_from_server);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // System.out.println(toFormat.format(date));
        ndate=toFormat.format(date);
        Log.d("Date_is", ""+ndate);

        return ndate;
    }







    public static String getDate_with_time(String msg_date)
    {
        long unixSeconds = Long.parseLong(msg_date);

        Date time=new Date((long)unixSeconds*1000);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");
        String date = sdf.format(time);
        Log.d("Dateis",": \t"+date);

        return date;

    }


    public static String getDate_only(String msg_date)
    {
        long unixSeconds = Long.parseLong(msg_date);

        Date time=new Date((long)unixSeconds*1000);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(time);
        Log.d("Dateis",": \t"+date);

        return date;

    }





}
