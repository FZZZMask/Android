package top.rayzhao98.fzzzmask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class GraphActivity extends MyBluetooth {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        ArrayList<String> myArrayList = null;
        if (intent != null) {
            myArrayList = intent.getStringArrayListExtra("data");
            Log.d("data", "onCreate: " + myArrayList);
        }
        DataPoint[] dataPoints = new DataPoint[myArrayList.size()];
        for (int i = 0; i < myArrayList.size(); i++) {
            dataPoints[i] = new DataPoint(i, Double.parseDouble(myArrayList.get(i)));
        }
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.addSeries(series);
    }
}
