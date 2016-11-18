package edu.uph.parkmore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuActivity extends Activity
{
    private TextView name_text;
    private Button reserve_button;
    private Button manage_button;
    private Button logout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        name_text = (TextView) findViewById(R.id.menu_name_text);
        reserve_button = (Button) findViewById(R.id.menu_reserve_button);
        manage_button = (Button) findViewById(R.id.menu_manage_button);
        logout_button = (Button) findViewById(R.id.menu_logout_button);

        logout_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    new LogoutAsyncTask().execute(new JSONObject().put("action", "logout").toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        reserve_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent i = new Intent(MenuActivity.this, ReserveActivity.class);
                startActivity(i);
            }
        });

        manage_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent i = new Intent(MenuActivity.this, ManageReservationActivity.class);
                startActivity(i);
            }
        });
        try
        {
            new GetUserDataAsyncTask().execute(new JSONObject().put("action", "get_user_data").toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    class LogoutAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MenuActivity.this, "", "Signing out...", true, false);
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
                Global.show_alert(MenuActivity.this, "Error", "Connection error.");
                return;
            }
            JSONObject json;
            try
            {
                json = new JSONObject(param);
                if (json.getBoolean("success"))
                {
                    finish();
                }
                else
                {
                    JSONArray error_codes = json.getJSONArray("error_codes");
                    String error_string = "";
                    for (int i = 0; i < error_codes.length(); i++)
                    {
                        error_string += error_codes.getString(i) + " ";
                    }
                    Global.show_alert(MenuActivity.this, "Error", error_string);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }

    class GetUserDataAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MenuActivity.this, "", "Retrieving user data...", true, false);
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
                Global.show_alert(MenuActivity.this, "Error", "Connection error.");
                return;
            }
            JSONObject json;
            try
            {
                json = new JSONObject(param);
                if (json.getBoolean("success"))
                {
                    String plate = json.getString("license_plate_number");
                    name_text.setText(json.getString("name") + (plate.equals("") ? "" : " (" + plate + ")"));
                }
                else
                {
                    JSONArray error_codes = json.getJSONArray("error_codes");
                    String error_string = "";
                    for (int i = 0; i < error_codes.length(); i++)
                    {
                        error_string += error_codes.getString(i) + " ";
                    }
                    Global.show_alert(MenuActivity.this, "Error", error_string);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Global.show_alert(MenuActivity.this, "Error", "JSON parsing error.");
                return;
            }
        }

    }
}
