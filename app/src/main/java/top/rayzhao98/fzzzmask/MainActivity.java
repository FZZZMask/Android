package top.rayzhao98.fzzzmask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

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
                Log.d("test", "onClick: " + test);
                Intent intent = new Intent(MainActivity.this, DustGraphActivity.class);
                intent.putExtra("type", "dust");
                intent.putExtra("data", dustArrayList);
                startActivity(intent);
            }
        });

        mainTemperatureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TemperatureGraphActivity.class);
                intent.putExtra("type", "temperature");
                intent.putExtra("data", temperatureArrayList);
                startActivity(intent);
            }
        });

        mainHumidityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HumidityGraphActivity.class);
                intent.putExtra("type", "humidity");
                intent.putExtra("data", humidityArrayList);
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

