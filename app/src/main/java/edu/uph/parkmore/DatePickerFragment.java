package edu.uph.parkmore;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import org.joda.time.LocalDate;

import java.util.Calendar;

/**
 * Created by User on 11/18/2016.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        LocalDate d = new LocalDate(year, month + 1, day);
        this.listener.on_date_picked(d);
    }


    public interface OnDatePickedListener
    {
        void on_date_picked(LocalDate date);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            this.listener = (OnDatePickedListener) activity;
        }
        catch (final ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnDatePickedListener.");
        }
    }

    private OnDatePickedListener listener;

}
