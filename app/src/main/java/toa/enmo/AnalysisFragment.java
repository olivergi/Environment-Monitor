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

/**
 * Created by iosdev on 22.9.2016.
 */

public class AnalysisFragment extends Fragment {
    View v;
    LineChart chart;
    Thread graphThread;
    String updateString = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.analysis_fragment, container, false);

        chart = (LineChart) v.findViewById(R.id.chart);
        chart.setDescriptionColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawLabels(true);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setTextSize(12);
        chart.setDescriptionTextSize(12);

        updateChart("temp");

        if (graphThread == null) {
            graphThread = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            sleep(2000);
                            getMA().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getGC().createDataShit();
                                    updateChart(updateString);
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

    public void updateChart(String data) {
        switch (data) {
            case "temp":
                updateString = "temp";
                getGC().createDataShit();
                chart.setData(getGC().tempData);
                chart.setDescription("Temperature");
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
            case "acc":
                updateString = "acc";
                getGC().createDataShit();
                chart.setData(getGC().accData);
                chart.setDescription("Acceleration");
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
            case "press":
                updateString = "press";
                getGC().createDataShit();
                chart.setData(getGC().pressData);
                chart.setDescription("Pressure");
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
            case "light":
                updateString = "light";
                getGC().createDataShit();
                chart.setData(getGC().lightData);
                chart.setDescription("Light");
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
        }
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
