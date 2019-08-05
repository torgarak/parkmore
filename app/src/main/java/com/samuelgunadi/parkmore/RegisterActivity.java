package com.samuelgunadi.parkmore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Sign Up Activity
 * @author Samuel I. Gunadi
 */
public class RegisterActivity extends Activity
{

    private Button register_button;
    private EditText email_edit;
    private EditText password_edit;
    private EditText password_confirm_edit;
    private EditText name_edit;
    private EditText license_plate_number_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        register_button = (Button) findViewById(R.id.register_button);

        email_edit = (EditText) findViewById(R.id.register_email_edit);
        password_edit = (EditText) findViewById(R.id.register_password_edit);
        password_confirm_edit = (EditText) findViewById(R.id.register_password_confirm_edit);
        name_edit = (EditText) findViewById(R.id.register_name_edit);
        license_plate_number_edit = (EditText) findViewById(R.id.register_license_plate_number_edit);

        register_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!password_confirm_edit.getText().toString().equals(password_edit.getText().toString()))
                {
                    Global.show_alert(RegisterActivity.this, "Error", "Password does not match the confirm password.");
                    return;
                }
                try
                {
                    new RegisterAsyncTask().execute(
                            new JSONObject().put("action", "register")
                                    .put("email", email_edit.getText().toString())
                                    .put("password", password_edit.getText().toString())
                                    .put("name", name_edit.getText().toString())
                                    .put("license_plate_number", license_plate_number_edit.getText().toString())
                                    .toString()
                    );
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Global.show_alert(RegisterActivity.this, "Error", "Connection error.");
                    return;
                }

            }
        });
    }

    class RegisterAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(RegisterActivity.this, "", "Signing up...", true, false);
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
                    Global.show_alert(RegisterActivity.this, "Error", "Connection error.");
                    return;
                }
                json = new JSONObject(param);
                if (json.getBoolean("success") == true)
                {
                    Global.show_alert(RegisterActivity.this, "Success", "Account created.", new Callable<Void>()
                    {
                        @Override
                        public Void call() throws Exception
                        {
                            RegisterActivity.this.finish();
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
                    Global.show_alert(RegisterActivity.this, "Error",  error_string);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Global.show_alert(RegisterActivity.this, "Error", "JSON parsing error.");
                return;
            }
        }

    }

}
