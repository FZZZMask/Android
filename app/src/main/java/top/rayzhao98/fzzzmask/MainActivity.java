package top.rayzhao98.fzzzmask;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends MyBluetooth {

    TextView mainPM25TextView;
    TextView mainWeatherTextView;
    TextView mainTemperatureTextView;
    TextView mainHumidityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPM25TextView = findViewById(R.id.mainPM25TextView);
        mainTemperatureTextView = findViewById(R.id.mainTemperatureTextView);
        mainHumidityTextView = findViewById(R.id.mainHumidityTextView);
        mainWeatherTextView = findViewById(R.id.mainWeatherTextView);

        super.mainPM25TextView = mainPM25TextView;
        super.mainTemperatureTextView = mainTemperatureTextView;
        super.mainHumidityTextView = mainHumidityTextView;
        super.mainWeatherTextView = mainWeatherTextView;

        mainPM25TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });
        getWeather();
        bluetoothfunction();
    }

    public void getWeather() {
        WeatherSearchQuery mquery = new WeatherSearchQuery("南京", WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {

            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
                LocalWeatherLive weatherlive = localWeatherLiveResult.getLiveResult();
                mainWeatherTextView.setText(weatherlive.getWeather());
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

            }
        });
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn();
    }
}

