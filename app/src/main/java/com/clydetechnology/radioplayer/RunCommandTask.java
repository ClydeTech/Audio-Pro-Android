package com.clydetechnology.radioplayer;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RunCommandTask {

    private String command;
    private Executor executor;
    private Handler handler;

    public RunCommandTask(String cmd) {
        command = cmd;
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public void execute() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result = runCommand();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(result);
                    }
                });
            }
        });
    }

    private String runCommand() {
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

    private void onPostExecute(String result) {
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
