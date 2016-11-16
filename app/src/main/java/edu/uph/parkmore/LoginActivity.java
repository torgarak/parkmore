package edu.uph.parkmore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity
{
    private EditText login_email_edit;
    private EditText login_password_edit;
    private TextView login_register_view;
    private Button login_button;

    private static final int PERMISSIONS_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, PERMISSIONS_ID);
        }
        setContentView(R.layout.login_activity);
        login_email_edit = (EditText) findViewById(R.id.login_email_edit);
        login_password_edit = (EditText) findViewById(R.id.login_password_edit);
        login_register_view = (TextView) findViewById(R.id.login_register_view);
        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    new LoginAsyncTask().execute(
                            new JSONObject()
                                    .put("action", "login")
                                    .put("email", login_email_edit.getText().toString())
                                    .put("password", login_password_edit.getText().toString())
                                    .toString()
                    );
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
        login_register_view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    class LoginAsyncTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(LoginActivity.this, "", "Signing in...", true, false);
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
                if(param == null)
                {
                    AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).create();
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
                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    finish();
                    startActivity(i);
                }
                else
                {
                    AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).create();
                    dlg.setTitle("Error");
                    dlg.setMessage("Incorrect email or password.");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSIONS_ID:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0)
                {
                    for (int el : grantResults)
                    {
                        if (el != PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this, "You're nuts.", Toast.LENGTH_LONG);
                            finish();
                        }
                    }
                }
                return;
            }
            default:
            {}
        }
    }
}
