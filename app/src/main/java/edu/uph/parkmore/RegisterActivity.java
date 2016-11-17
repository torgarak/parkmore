package edu.uph.parkmore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import static android.R.attr.duration;
import static android.R.attr.text;
import static edu.uph.parkmore.R.id.login_button;
import static edu.uph.parkmore.R.id.login_email_edit;
import static edu.uph.parkmore.R.id.login_password_edit;
import static edu.uph.parkmore.R.id.register_email_edit;
import static edu.uph.parkmore.R.id.register_password_edit;

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
            return Global.send_post_request(params[0]);
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
            }
        }

    }

}
