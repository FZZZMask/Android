package top.rayzhao98.fzzzmask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ray on 2018/11/18.
 */

public class MyBluetooth extends AppCompatActivity {
    private MyBluetooth.readThread thread;
    MyBluetooth.MyHandler handler = new MyBluetooth.MyHandler();
    BluetoothSocket clientSocket;
    TextView mainPM25TextView;
    TextView mainWeatherTextView;
    TextView mainTemperatureTextView;
    TextView mainHumidityTextView;
    public String test = "233";

    public ArrayList<String> dustArrayList = new ArrayList<String>();
    public ArrayList<String> temperatureArrayList = new ArrayList<String>();
    public ArrayList<String> humidityArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MyHandler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    public ArrayList<String> getDustArrayList() {
        return dustArrayList;
    }

    public ArrayList<String> getTemperatureArrayList() {
        return temperatureArrayList;
    }

    public ArrayList<String> getHumidityArrayList() {
        return humidityArrayList;
    }

    public void bluetoothfunction() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        String address = "98:D3:21:FC:75:93";
        int REQUEST_ENABLE_BT = 1;
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        List<String> devices = new ArrayList<String>();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            devices.add(device.getName() + "-" + device.getAddress());
            Log.d("233", "bluetoothfunction() returned: " + device.getName());
            Log.d("233", "bluetoothfunction: " + device.getAddress());
            if (device.getName() == "HC-05") {
                address = device.getAddress();
                break;
            }
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        try {
            clientSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            clientSocket.connect();
            thread = new readThread();
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class readThread extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;
            try {
                mmInStream = clientSocket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String b = "";
            while (true) {
                try {
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        b += new String(buf_data);
                        String dust = "";
                        String temperature = "";
                        String humidity = "";
                        if (b.indexOf("\r") >= 0) {
                            Log.d("res", "run: " + b);
                            JSONObject jsonb = null;
                            try {
                                jsonb = new JSONObject(b);
                                Log.d("json", "run: " + jsonb.getString("dust"));
                                dust = jsonb.getString("dust");
                                temperature = jsonb.getString("temperature");
                                humidity = jsonb.getString("humidity");

                                final String breath = jsonb.getString("breath");
                                final String finalDust = dust;
                                final String finalTemperature = temperature;
                                final String finalHumidity = humidity;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainPM25TextView.setText(finalDust + "μg/m³");
                                        mainTemperatureTextView.setText(finalTemperature + " ℃");
                                        mainHumidityTextView.setText(finalHumidity + "%");

                                        dustArrayList.add(finalDust);
                                        temperatureArrayList.add(finalTemperature);
                                        humidityArrayList.add(finalHumidity);

                                        OkHttpClient client = new OkHttpClient();
                                        FormBody formBody = new FormBody.Builder()
                                                .add("user", "5")
                                                .add("latitude", "")
                                                .add("longitude", "")
                                                .add("is_cold", "False")
                                                .add("pm25_value", finalDust)
                                                .add("is_health", "True")
                                                .build();
                                        Request request = new Request.Builder()
                                                .url(ApiConfig.API_ROOT + "/msg/")
                                                .addHeader("Authorization", "Token " + ApiConfig.KEY)
                                                .addHeader("Content-Type", "application/json")
                                                .post(formBody)
                                                .build();
//                                        Request request = new Request.Builder()
//                                                .get()
//                                                .url("http://192.168.1.103:8000/api/v1/visual/health/")
//                                                .build();
                                        Call call = client.newCall(request);
                                        call.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.d("fail", "onFailure: " + "fail");
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                Log.d("res", "onResponse: " + response.body().string());
                                            }
                                        });


//                                        uploadMsg(finalDust);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Message msg = new Message();
                            msg.obj = b;
                            msg.what = 1;
                            handler.sendMessage(msg);
                            b = "";
                        }

                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public void uploadMsg(String dust) {


//        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        double latitude = 0;
//        double longitude = 0;
//        AMapLocationClient mLocationClient = null;
//        AMapLocationListener mLocationListener = new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//
//            }
//        };
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        mLocationClient.setLocationListener(mLocationListener);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;// 指定LocationManager的定位方法
//NETWORK_PROVIDER 网络定位、GPS_PROVIDER GPS定位
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);// 调用getLastKnownLocation()方法获取当前的位置信息
//        double lat = location.getLatitude();//获取纬度
//        double lng = location.getLongitude();//获取经度

        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("user", "5")
                .add("latitude", "")
                .add("longitude", String.valueOf(100))
                .add("is_cold", "False")
                .add("pm25_value", dust)
                .add("is_health", "True")
                .build();
        Request request = new Request.Builder()
                .get()
                .url(ApiConfig.API_ROOT)
//                .addHeader("Authorization", "Token " + ApiConfig.KEY)
//                .addHeader("Content-Type", "application/json")
//                .post(formBody)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "onFailure: " + "fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("response", "onResponse: " + response.body().string());
            }
        });
    }



    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    }
}
