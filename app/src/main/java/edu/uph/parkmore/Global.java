package edu.uph.parkmore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.concurrent.Callable;


/**
 * Useful Functions
 * @author Samuel I. Gunadi
 */
public class Global
{

    public static void show_alert(Context context, String title, String message)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("Okay", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
    public static void show_alert(Context context, String title, String message, final Callable<Void> onclick)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("Okay", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        try
                        {
                            onclick.call();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }


    public static DateTime round_minutes(final DateTime dt, final int minutes)
    {
        if (minutes < 1 || 60 % minutes != 0)
        {
            throw new IllegalArgumentException("minutes must be a factor of 60");
        }

        final DateTime hour = dt.hourOfDay().roundFloorCopy();
        final long millis_since_hour = new Duration(hour, dt).getMillis();
        final int rounded_minutes = ((int)Math.round(
                millis_since_hour / 60000.0 / minutes)) * minutes;
        return hour.plusMinutes(rounded_minutes);
    }

}
