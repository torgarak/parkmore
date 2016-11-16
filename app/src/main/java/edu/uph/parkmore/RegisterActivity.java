package edu.uph.parkmore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                    AlertDialog dlg = new AlertDialog.Builder(RegisterActivity.this).create();
                    dlg.setTitle("Error");
                    dlg.setMessage("Password does not match the confirm password.");
                    dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    dlg.show();
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
            HttpURLConnection url_connection = null;
            BufferedReader reader = null;
            String response;
            try
            {
                url_connection = (HttpURLConnection) new URL(Global.SERVER_URL).openConnection();
                url_connection.setDoOutput(true);
                url_connection.setRequestMethod("POST");
                url_connection.setRequestProperty("Content-Type", "application/json");
                url_connection.setRequestProperty("Accept", "application/json");
                url_connection.setRequestProperty("Accept-Charset", "utf-8");
                Writer writer = new BufferedWriter(new OutputStreamWriter(url_connection.getOutputStream(), "UTF-8"));
                writer.write(params[0]);
                writer.close();

                InputStream inputstream = url_connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputstream == null)
                {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputstream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }
                reader.close();
                if (buffer.length() == 0)
                {
                    return null;
                }
                response = buffer.toString();
                return response;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (url_connection != null)
                {
                    url_connection.disconnect();
                }
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (final IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            return null;
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
                    AlertDialog dlg = new AlertDialog.Builder(RegisterActivity.this).create();
                    dlg.setTitle("Error");
                    dlg.setMessage("Empty response.");
                    dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            }
                    );
                    dlg.show();
                    return;
                }
                json = new JSONObject(param);
                if (json.getBoolean("success") == true)
                {
                    AlertDialog dlg = new AlertDialog.Builder(RegisterActivity.this).create();
                    dlg.setTitle("Success");
                    dlg.setMessage("Account created.");
                    dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    RegisterActivity.this.finish();
                                }
                            }
                    );
                    dlg.show();
                    return;

                }
                else
                {
                    AlertDialog dlg = new AlertDialog.Builder(RegisterActivity.this).create();
                    dlg.setTitle("Error");
                    dlg.setMessage("Failed.");
                    dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            }
                    );
                    dlg.show();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }

}
