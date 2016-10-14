package toa.enmo;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.*;

import java.util.ArrayList;

/**
 * Created by iosdev on 22.9.2016.
 */

public class AnalysisFragment extends Fragment {
    View v;
    LineChart chart;
    Thread graphThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.analysis_fragment, container, false);

        chart = (LineChart) v.findViewById(R.id.chart);
        chart.setDescriptionColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getLegend().setTextColor(Color.WHITE);

        getGC().createDataShit();
        chart.setData(getGC().tempData);

        if (graphThread == null) {
            graphThread = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            sleep(3000);
                            getMA().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getGC().createDataShit();
                                    chart.setData(getGC().tempData);
                                    chart.notifyDataSetChanged();
                                    chart.invalidate();
                                }
                            });
                        } catch (Exception e) {
                            Log.d("Error", "tempthread:" + e);
                        }
                    }
                }
            };
            graphThread.start();
        }


        return v;
    }


    private SensorControl getSC(){
        return ((MainActivity)getActivity()).sc;
    }

    private BluetoothControl getBC(){
        return ((MainActivity)getActivity()).bc;
    }

    private GraphControl getGC() {
        return ((MainActivity) getActivity()).gc;
    }

    private MainActivity getMA() {
        return (MainActivity)getActivity();
    }

}
