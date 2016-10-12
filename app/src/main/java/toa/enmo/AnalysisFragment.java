package toa.enmo;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
    ArrayList<Entry> entriesAccInt = new ArrayList<Entry>();
    ArrayList<Entry> entriesAccExt = new ArrayList<Entry>();
    ArrayList<LineDataSet> accArray = new ArrayList<LineDataSet>();
    int counter = 0;
    LineChart chart;
    LineData lineData;
    LineDataSet dataSetAccInt;
    LineDataSet dataSetAccExt;
    LineDataSet dataSetTempInt;
    LineDataSet dataSetTempExt;
    LineDataSet dataSetLightInt;
    LineDataSet dataSetLightExt;
    LineDataSet dataSetPressureInt;
    LineDataSet dataSetPressureExt;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.analysis_fragment, container, false);

        chart = (LineChart) v.findViewById(R.id.chart);
        chart.setDescription("Acceleration");
        chart.setDescriptionColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        /*for (Float data : getMA().accList ) {
            // turn your data into Entry objects
            entriesAccInt.add(new Entry(counter * 10, data));
            counter++;
        }
        dataSetAccInt = new LineDataSet(entriesAccInt, "Acceleration"); // add entries to dataset
        dataSetAccInt.setValueTextColor(Color.WHITE);
        dataSetAccInt.setDrawCircles(false);
        dataSetAccInt.setDrawValues(false);
        lineData = new LineData(dataSetAccInt);
        chart.setData(lineData);
        chart.invalidate();*/

        return v;
    }
    private SensorControl getSC(){
        return ((MainActivity)getActivity()).sc;
    }


    private BluetoothControl getBC(){
        return ((MainActivity)getActivity()).bc;
    }


    private MainActivity getMA() {
        return (MainActivity)getActivity();
    }
    public void createDataShit(){
        entriesAccInt.add(new Entry(counter * 10, getSC().tempAcc));
        //entriesAccExt.add(new Entry(counter * 10, getBC().acceleration));

        dataSetAccInt = new LineDataSet(entriesAccInt, "Internal Acceleration");
        dataSetAccInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetAccExt = new LineDataSet(entriesAccExt, "External Acceleration");
        dataSetAccExt.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetAccInt);
        dataSets.add(dataSetAccExt);
        LineData data = new LineData(dataSets);
        chart.setData(data);

        dataSetAccInt.setValueTextColor(Color.WHITE);
        dataSetAccInt.setDrawCircles(false);
        dataSetAccInt.setDrawValues(false);
        System.out.println("Should update the list here.");
        chart.invalidate();
        /*

        data.notifyDataChanged();
        chart.notifyDataSetChanged();
         */
    }
}
