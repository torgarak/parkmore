package com.samuelgunadi.parkmore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.concurrent.Callable;


/**
 * Create Reservation Activity
 * @author Samuel I. Gunadi
 */
public class ReserveActivity extends Activity implements DatePickerFragment.OnDatePickedListener, TimePickerFragment.OnTimePickedListener
{

    private TextView start_dt_edit;
    private TextView end_dt_edit;
    private TextView price_text;
    private Button reserve_button;
    private LocalDate date;
    private LocalTime time;
    private DateTimeFormatter sql_fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter local_fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    boolean set_start = false;
    public static LocalDateTime start_dt;
    public static LocalDateTime end_dt;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_activity);

        start_dt_edit = (TextView) findViewById(R.id.reserve_start_dt_edit);
        end_dt_edit = (TextView) findViewById(R.id.reserve_end_dt_edit);
        price_text = (TextView) findViewById(R.id.reserve_price_text);
        reserve_button = (Button) findViewById(R.id.reserve_button);
        LocalDateTime dt = Global.round_minutes(new DateTime(), 10).toLocalDateTime();
        start_dt_edit.setText(dt.toString(local_fmt));
        end_dt_edit.setText(dt.plusMinutes(10).toString(local_fmt));
        start_dt_edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                set_start = true;
                new DatePickerFragment().show(getFragmentManager(), "date_picker");
            }
        });
        end_dt_edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                set_start = false;
                new DatePickerFragment().show(getFragmentManager(), "date_picker");
            }
        });
        reserve_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    LocalDateTime start_dt = local_fmt.parseLocalDateTime(start_dt_edit.getText().toString());
                    LocalDateTime end_dt = local_fmt.parseLocalDateTime(end_dt_edit.getText().toString());
                    DateTime start_utc = start_dt.toDateTime().withZone(DateTimeZone.UTC);
                    DateTime end_utc = end_dt.toDateTime().withZone(DateTimeZone.UTC);
                    // convert to UTC
                    new ReserveAsyncTask().execute(
                            new JSONObject()
                                    .put("action", "reserve")
                                    .put("start_utc", start_utc.toString(sql_fmt))
                                    .put("end_utc", end_utc.toString(sql_fmt))
                                    .toString()
                    );
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Global.show_alert(ReserveActivity.this, "Error", "Connection error.");
                    return;
                }
            }
        });
    }

    @Override
    public void on_date_picked(LocalDate date)
    {
        this.date = date;
        new TimePickerFragment().show(getFragmentManager(), "time_picker");
    }

    @Override
    public void on_time_picked(LocalTime time)
    {
        this.time = time;
        //merge
        LocalDateTime dt = Global.round_minutes(date.toLocalDateTime(time).toDateTime(), 10).toLocalDateTime();
        if (set_start)
        {
            start_dt_edit.setText(dt.toString(local_fmt));
        }
        else
        {
            end_dt_edit.setText(dt.toString(local_fmt));
        }

        start_dt = local_fmt.parseLocalDateTime(start_dt_edit.getText().toString());
        end_dt = local_fmt.parseLocalDateTime(end_dt_edit.getText().toString());
        price_text.setText(NumberFormat.getCurrencyInstance().format(BigDecimal.valueOf(Minutes.minutesBetween(start_dt, end_dt).getMinutes()).divide(BigDecimal.valueOf(6.0), RoundingMode.CEILING)));
    }

    class ReserveAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            start_dt = local_fmt.parseLocalDateTime(start_dt_edit.getText().toString());
            progress = ProgressDialog.show(ReserveActivity.this, "", "Processing...", true, false);
        }

        @Override
        protected String doInBackground(String... params)
        {
            return ParkmoreClient.send_and_receive_json(params[0]);
        }


        @Override
        protected void onPostExecute(String param)
        {
            if (progress.isShowing())
                progress.dismiss();
            JSONObject json;
            try
            {
                if (param == null)
                {
                    Global.show_alert(ReserveActivity.this, "Error", "Connection error.");
                    return;
                }
                json = new JSONObject(param);
                if (json.getBoolean("success") == true)
                {
                    Global.show_alert(ReserveActivity.this, "Success", "Reservation created.", new Callable<Void>()
                    {
                        @Override
                        public Void call() throws Exception
                        {
                            ReserveActivity.this.finish();
                            return null;
                        }
                    });
                    return;

                }
                else
                {
                    JSONArray error_codes = json.getJSONArray("error_codes");
                    String error_string = "";
                    for (int i = 0; i < error_codes.length(); i++)
                    {
                        error_string += error_codes.getString(i) + " ";
                    }
                    Global.show_alert(ReserveActivity.this, "Error",  error_string);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Global.show_alert(ReserveActivity.this, "Error", "JSON parsing error.");
                return;
            }
        }

    }
}
