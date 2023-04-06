package com.clydetechnology.radioplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.clydetechnology.radioplayer.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SeekBar volumeBar = findViewById(R.id.volumeBar);
        final LinearLayout stationsContainer = findViewById(R.id.stationsContainer);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Make API call to get player status
                //JSONObject status = new RunCommandTask("getPlayerStatus").execute();
                final int volume = 5; //status.getInt("vol");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Set volume bar to current volume
                        volumeBar.setProgress(volume);
                    }
                });

                // Build list of stations
                List<Station> stations = new ArrayList<>();
                try {
                    InputStream is = getResources().openRawResource(R.raw.stations);
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    is.close();

                    String json = new String(buffer, "UTF-8");
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String url = jsonObject.getString("url");
                        stations.add(new Station(title, url));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Add stations to container
                        for (Station station : stations) {
                            View stationButton = getLayoutInflater().inflate(R.layout.station_button, null);
                            stationButton.setLayoutParams(params);
                            stationButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Make API call to play station
                                    String command = "setPlayerCmd:play:" + station.url;

                                    new RunCommandTask(command).execute();
                                }
                            });

                            stationsContainer.addView(stationButton);

                            // Set station button text
                            int childCount = stationsContainer.getChildCount();
                            View childView = stationsContainer.getChildAt(childCount - 1);
                            if (childView instanceof StationButton) {
                                StationButton stationBtn = (StationButton) childView;
                                stationBtn.setText(station.title);
                            }

                        }
                    }
                });
            }
        });
        thread.start();
    }
}