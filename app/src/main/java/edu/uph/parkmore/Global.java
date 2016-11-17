package edu.uph.parkmore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Created by User on 11/16/2016.
 */

public class Global
{
    public static final String SERVER_URL = "http://192.168.2.173/api/";

    // cookie is for php session id
    public static CookieManager cookie_mgr = new CookieManager();

    public static String send_post_request(String json_string)
    {
        HttpURLConnection connection = null;
        try
        {
            connection = (HttpURLConnection) new URL(Global.SERVER_URL).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            if (Global.cookie_mgr.getCookieStore().getCookies().size() > 0)
            {
                connection.setRequestProperty("Cookie", TextUtils.join(";", Global.cookie_mgr.getCookieStore().getCookies()));
            }
            Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(json_string);
            writer.close();
            // get cookies
            Map<String, List<String>> header_fields = connection.getHeaderFields();
            List<String> cookies = header_fields.get("Set-Cookie");
            if (cookies != null)
            {
                for (String cookie : cookies)
                {
                    Global.cookie_mgr.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }
            // get server response
            InputStream inputstream = connection.getInputStream();
            Scanner s = new java.util.Scanner(inputstream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
        return null;
    }
    public static void show_alert(Context context, String title, String message)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("KTHXBAI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
    public static void show_alert(Context context, String title, String message, final Callable<Void> onclick)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("KTHXBAI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        try
                        {
                            onclick.call();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}
