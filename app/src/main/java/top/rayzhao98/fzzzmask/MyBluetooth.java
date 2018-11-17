package top.rayzhao98.fzzzmask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        if(b.indexOf("\r")>=0){
                            Log.d("res", "run: " + b);
                            JSONObject jsonb = null;
                            try {
                                jsonb = new JSONObject(b);
                                Log.d("json", "run: " + jsonb.getString("dust"));
                                final String dust = jsonb.getString("dust") + "μg/m³";
                                final String temperature = jsonb.getString("temperature") + " ℃";
                                final String humidity = jsonb.getString("humidity") + "%";
                                final String breath = jsonb.getString("breath");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainPM25TextView.setText(dust);
                                        mainTemperatureTextView.setText(temperature);
                                        mainHumidityTextView.setText(humidity);
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

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    }
}
