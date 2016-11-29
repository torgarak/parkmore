package edu.uph.parkmore;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import java.util.Calendar;


/**
 * Time Picker Fragment
 * @author Samuel I. Gunadi
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // use the current time as the default values for the picker
        // create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 0, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hour_of_day, int minute)
    {
        LocalTime t = new LocalTime(hour_of_day, minute);
        this.listener.on_time_picked(t);
    }

    public interface OnTimePickedListener
    {
        void on_time_picked(LocalTime time);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            this.listener = (OnTimePickedListener) activity;
        }
        catch (final ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnDatePickedListener.");
        }
    }

    private OnTimePickedListener listener;


}