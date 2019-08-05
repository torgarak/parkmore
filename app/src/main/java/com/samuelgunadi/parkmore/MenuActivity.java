package com.samuelgunadi.parkmore;

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


/**
 * Menu Activity
 * @author Samuel I. Gunadi
 */
public class MenuActivity extends Activity
{
    private TextView name_text;
    private Button view_button;
    private Button reserve_button;
    private Button extend_button;
    private Button cancel_button;
    private Button check_in_button;
    private Button check_out_button;
    private Button logout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        name_text = (TextView) findViewById(R.id.menu_name_text);
        view_button = (Button) findViewById(R.id.menu_view_button);
        reserve_button = (Button) findViewById(R.id.menu_reserve_button);
        extend_button = (Button) findViewById(R.id.menu_extend_button);
        cancel_button = (Button) findViewById(R.id.menu_cancel_button);
        check_in_button = (Button) findViewById(R.id.menu_check_in_button);
        check_out_button = (Button) findViewById(R.id.menu_check_out_button);
        logout_button = (Button) findViewById(R.id.menu_logout_button);

        view_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MenuActivity.this, ViewActivity.class);
                startActivity(i);
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

        extend_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent i = new Intent(MenuActivity.this, ExtendActivity.class);
                startActivity(i);
            }
        });


        cancel_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    new CancelAsyncTask().execute(new JSONObject().put("action", "cancel").toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        check_in_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    new CheckInAsyncTask().execute(new JSONObject().put("action", "checkin").toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        check_out_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    new CheckOutAsyncTask().execute(new JSONObject().put("action", "checkout").toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

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

    class CheckInAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MenuActivity.this, "", "Checking in...", true, false);
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
                    Global.show_alert(MenuActivity.this, "Success", "Checked in.");
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

    class CheckOutAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MenuActivity.this, "", "Checking out...", true, false);
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
                    Global.show_alert(MenuActivity.this, "Success", "Checked out.");
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

    class CancelAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MenuActivity.this, "", "Cancelling...", true, false);
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
                    Global.show_alert(MenuActivity.this, "Success", "Your reservation has been cancelled ");
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
