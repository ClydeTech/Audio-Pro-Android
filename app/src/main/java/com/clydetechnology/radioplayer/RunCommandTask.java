package com.clydetechnology.radioplayer;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RunCommandTask extends AsyncTask<Void, Void, String> {

    private String command;

    public RunCommandTask(String cmd) {
        command = cmd;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String url = "http://192.168.0.5/httpapi.asp?command=" + command;

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL requestUrl = new URL(url);
                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                StringBuilder response = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject status = new JSONObject(result);
                int vol = status.getInt("vol");
                // Do something with vol
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Handle error
        }
    }
}
