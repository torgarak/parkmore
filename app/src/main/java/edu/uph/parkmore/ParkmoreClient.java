package edu.uph.parkmore;

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

/**
 * Created by User on 11/16/2016.
 */

public class ParkmoreClient
{
    public static String server_url = "http://127.0.0.1/api/";

    // cookie is for php session id
    private static CookieManager cookie_mgr = new CookieManager();

    public static String send_and_receive_json(String json_string)
    {
        HttpURLConnection connection = null;
        try
        {
            connection = (HttpURLConnection) new URL(ParkmoreClient.server_url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            if (ParkmoreClient.cookie_mgr.getCookieStore().getCookies().size() > 0)
            {
                connection.setRequestProperty("Cookie", TextUtils.join(";", ParkmoreClient.cookie_mgr.getCookieStore().getCookies()));
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
                    ParkmoreClient.cookie_mgr.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }
            // get server response
            InputStream inputstream = connection.getInputStream();
            Scanner s = new java.util.Scanner(inputstream).useDelimiter("\\A");
            String tok = s.hasNext() ? s.next() : "";
            inputstream.close();
            return tok;
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
}
