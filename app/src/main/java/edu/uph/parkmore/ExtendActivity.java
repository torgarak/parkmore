package edu.uph.parkmore;

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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class ExtendActivity extends Activity implements DatePickerFragment.OnDatePickedListener, TimePickerFragment.OnTimePickedListener
{

    private TextView end_dt_edit;
    private Button extend_button;
    private LocalDate date;
    private LocalTime time;
    private DateTimeFormatter sql_fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter local_fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extend_activity);
        end_dt_edit = (TextView) findViewById(R.id.extend_end_dt_edit);
        extend_button = (Button) findViewById(R.id.reserve_button);
        LocalDateTime dt = Global.round_minutes(new DateTime(), 10).toLocalDateTime();
        end_dt_edit.setText(dt.plusMinutes(10).toString(local_fmt));
        end_dt_edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DatePickerFragment().show(getFragmentManager(), "date_picker");
            }
        });

        extend_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    LocalDateTime end_dt = local_fmt.parseLocalDateTime(end_dt_edit.getText().toString());
                    DateTime end_utc = end_dt.toDateTime().withZone(DateTimeZone.UTC);
                    // convert to UTC
                    new ExtendAsyncTask().execute(new JSONObject().put("action", "extend").put("end_utc", end_utc.toString(sql_fmt)).toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Global.show_alert(ExtendActivity.this, "Error", "Connection error.");
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
        end_dt_edit.setText(dt.toString(local_fmt));
    }

    class ExtendAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ExtendActivity.this, "", "Processing...", true, false);
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
                    Global.show_alert(ExtendActivity.this, "Error", "Connection error.");
                    return;
                }
                json = new JSONObject(param);
                if (json.getBoolean("success") == true)
                {
                    Global.show_alert(ExtendActivity.this, "Success", "Reservation extended.", new Callable<Void>()
                    {
                        @Override
                        public Void call() throws Exception
                        {
                            ExtendActivity.this.finish();
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
                    Global.show_alert(ExtendActivity.this, "Error",  error_string);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Global.show_alert(ExtendActivity.this, "Error", "JSON parsing error.");
                return;
            }
        }

    }
}
