package com.samuelgunadi.parkmore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static org.joda.time.DateTimeZone.UTC;

/**
 * View Reservation Activity
 * @author Samuel I. Gunadi
 */
public class ViewActivity extends Activity
{

    private DateTimeFormatter sql_fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter local_fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    private TextView reservation_text;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        reservation_text = (TextView) findViewById(R.id.view_reservation_text);;

        try
        {
            new ViewAsyncTask().execute(new JSONObject().put("action", "get_reservation_data").toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    class ViewAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ViewActivity.this, "", "Retrieving data...", true, false);
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
            {
                progress.dismiss();
            }
            if (param == null)
            {
                Global.show_alert(ViewActivity.this, "Error", "Connection error.");
                return;
            }
            JSONObject json;
            try
            {
                json = new JSONObject(param);
                if (json.getBoolean("success"))
                {
                    // convert server UTC time to local time
                    LocalDateTime start_utc = new DateTime(sql_fmt.parseDateTime(json.getString("start_utc")).toInstant(), UTC).toLocalDateTime();
                    LocalDateTime end_utc = new DateTime(sql_fmt.parseDateTime(json.getString("end_utc")).toInstant(), UTC).toLocalDateTime();
                    LocalDateTime timestamp_utc = new DateTime(sql_fmt.parseDateTime(json.getString("timestamp")).toInstant(), DateTimeZone.UTC).toLocalDateTime();

                    int state = json.getInt("state");
                    String state_str = "";
                    // state
                    // 0 = initial state
                    // 1 = reserved
                    // 2 = checked_in
                    // 3 = checked_out
                    // 4 = canceled
                    // 5 = auto_cancelled_not_checked_in
                    // 6 = auto_cancelled_checked_in
                    switch (state)
                    {
                        case 0:
                            state_str = "INITIAL_STATE";
                            break;
                        case 1:
                            state_str = "RESERVED";
                            break;
                        case 2:
                            state_str = "CHECKED_IN";
                            break;
                        case 3:
                            state_str = "CHECKED_OUT";
                            break;
                        case 4:
                            state_str = "CANCELED";
                            break;
                        case 5:
                            state_str = "AUTO_CANCELLED_NOT_CHECKED_IN";
                            break;
                        case 6:
                            state_str = "AUTO_CANCELLED_CHECKED_IN";
                            break;
                        default:
                    }

                    reservation_text.setText(
                            "Reservation code: "+ String.valueOf(json.getLong("reservation_id")) +  System.getProperty("line.separator") +
                                    "From: " + start_utc.toString(local_fmt) + System.getProperty("line.separator") +
                                    "Until: " +end_utc.toString(local_fmt) + System.getProperty("line.separator") +
                                    "Reserved at: " + timestamp_utc.toString(local_fmt) +System.getProperty("line.separator") +
                                    "State: " + state_str);
                }
                else
                {
                    JSONArray error_codes = json.getJSONArray("error_codes");
                    String error_string = "";
                    for (int i = 0; i < error_codes.length(); i++)
                    {
                        if (error_codes.getString(i) == "not_found")
                        {
                            reservation_text.setText("No reservation data found.");
                        }
                        else
                        {
                            error_string += error_codes.getString(i) + " ";
                        }
                    }
                    Global.show_alert(ViewActivity.this, "Error", error_string);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }

}
