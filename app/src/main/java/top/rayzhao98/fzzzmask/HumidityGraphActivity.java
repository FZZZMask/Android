package top.rayzhao98.fzzzmask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class HumidityGraphActivity extends MyBluetooth {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity_graph);

        Intent intent = getIntent();
        ArrayList<String> myArrayList = null;
        if (intent != null) {
            myArrayList = intent.getStringArrayListExtra("data");
            Log.d("data", "onCreate: " + myArrayList);
        }
        DataPoint[] dataPoints = new DataPoint[myArrayList.size() + humidityArrayList.size()];
        for (int i = 0; i < myArrayList.size(); i++) {
            dataPoints[i] = new DataPoint(i, Double.parseDouble(myArrayList.get(i)));
        }
        for (int i = 0; i < humidityArrayList.size(); i++) {
            dataPoints[i] = new DataPoint(i + myArrayList.size(), Double.parseDouble(humidityArrayList.get(i)));
        }
        GraphView graph = (GraphView) findViewById(R.id.humidityGraph);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setMinY(0);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.addSeries(series);
    }
}
