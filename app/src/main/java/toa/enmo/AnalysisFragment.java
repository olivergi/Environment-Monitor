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
    ArrayList<Entry> entriesAccInt = new ArrayList<Entry>();
    ArrayList<Entry> entriesAccExt = new ArrayList<Entry>();
    ArrayList<Entry> entriesPresInt = new ArrayList<Entry>();
    ArrayList<Entry> entriesPresExt = new ArrayList<Entry>();
    ArrayList<Entry> entriesLightInt = new ArrayList<Entry>();
    ArrayList<Entry> entriesLightExt = new ArrayList<Entry>();
    ArrayList<Entry> entriesTempInt = new ArrayList<Entry>();
    ArrayList<Entry> entriesTempExt = new ArrayList<Entry>();

    int counter = 0;
    LineChart chart;
    LineData accData;
    LineData pressData;
    LineData lightData;
    LineData tempData;
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
        //chart.setDescription("Acceleration");
        chart.setDescriptionColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        createDataShit();
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


        Thread tempThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        if (chart != null) {
                            System.out.println("Here is the ext temp: " + entriesTempExt.get(0));
                            addDataShit();
                            notifyAllChanged();
                        }

                    } catch (Exception e) {
                        Log.d("Error", "tempthread:" + e);
                    }
                }
            }
        };
        tempThread.start();

        return v;
    }

    private SensorControl getSC(){
        return ((MainActivity)getActivity()).sc;
    }


    private BluetoothControl getBC(){
        return ((MainActivity)getActivity()).bc;
    }

    public void notifyAllChanged() {
        dataSetAccExt.notifyDataSetChanged();
        dataSetAccInt.notifyDataSetChanged();

        dataSetPressureExt.notifyDataSetChanged();
        dataSetPressureInt.notifyDataSetChanged();

        dataSetLightExt.notifyDataSetChanged();
        dataSetLightInt.notifyDataSetChanged();

        dataSetTempExt.notifyDataSetChanged();
        dataSetTempInt.notifyDataSetChanged();

        chart.notifyDataSetChanged();
        chart.invalidate();
    }


    private MainActivity getMA() {
        return (MainActivity)getActivity();
    }


    public void addDataShit() {
        dataSetAccInt.addEntry(new Entry(counter * 10, getSC().acceleration));
        dataSetAccExt.addEntry(new Entry(counter * 10, getBC().accValue));

        dataSetPressureInt.addEntry(new Entry(counter * 10, getSC().pressure));
        dataSetPressureExt.addEntry(new Entry(counter * 10, getBC().pressValue));

        dataSetLightInt.addEntry(new Entry(counter * 10, getSC().light));
        dataSetLightExt.addEntry(new Entry(counter * 10, getBC().lightValue));

        dataSetTempInt.addEntry(new Entry(counter * 10, getSC().temperature));
        dataSetTempExt.addEntry(new Entry(counter * 10, getBC().tempValue));
        counter++;
    }

   /* public void fuckThisShit(ArrayList<Entry> entries, ArrayList<Entry> entries2,
                             LineDataSet dataset, LineDataSet dataset2,
                             ) {

    }*/

    public void createDataShit(){
        entriesAccInt.add(new Entry(counter * 10, getSC().acceleration));
        entriesAccExt.add(new Entry(counter * 10, getBC().accValue));
        entriesPresInt.add(new Entry(counter * 10, getSC().pressure));
        entriesPresExt.add(new Entry(counter * 10, getBC().pressValue));
        entriesLightInt.add(new Entry(counter * 10, getSC().light));
        entriesLightExt.add(new Entry(counter * 10, getBC().lightValue));
        entriesTempInt.add(new Entry(counter * 10, getSC().temperature));
        entriesTempExt.add(new Entry(counter * 10, getBC().tempValue));
        counter++;

        dataSetAccInt = new LineDataSet(entriesAccInt, "Internal Acceleration");
        dataSetAccInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetAccExt = new LineDataSet(entriesAccExt, "External Acceleration");
        dataSetAccExt.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSetPressureInt = new LineDataSet(entriesPresInt, "Internal Pressure");
        dataSetPressureInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetPressureExt = new LineDataSet(entriesPresExt, "External Pressure");
        dataSetPressureExt.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSetLightInt = new LineDataSet(entriesLightInt, "Internal Lux");
        dataSetLightInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetLightExt = new LineDataSet(entriesLightExt, "External Lux");
        dataSetLightExt.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSetTempInt = new LineDataSet(entriesTempInt, "Internal Temperature");
        dataSetTempInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetTempExt = new LineDataSet(entriesTempExt, "External Temperature");
        dataSetTempExt.setAxisDependency(YAxis.AxisDependency.LEFT);


        ArrayList<ILineDataSet> accDataSets = new ArrayList<>();
        accDataSets.add(dataSetAccInt);
        accDataSets.add(dataSetAccExt);

        ArrayList<ILineDataSet> pressDataSets = new ArrayList<>();
        pressDataSets.add(dataSetAccInt);
        pressDataSets.add(dataSetAccExt);

        ArrayList<ILineDataSet> lightDataSets = new ArrayList<>();
        lightDataSets.add(dataSetAccInt);
        lightDataSets.add(dataSetAccExt);

        ArrayList<ILineDataSet> tempDataSets = new ArrayList<>();
        tempDataSets.add(dataSetAccInt);
        tempDataSets.add(dataSetAccExt);

        accData = new LineData(accDataSets);
        pressData = new LineData(pressDataSets);
        lightData = new LineData(lightDataSets);
        tempData = new LineData(tempDataSets);
        chart.setData(tempData);


        dataSetAccInt.setValueTextColor(Color.WHITE);
        dataSetAccInt.setDrawCircles(false);
        dataSetAccInt.setDrawValues(false);
        dataSetAccExt.setValueTextColor(Color.WHITE);
        dataSetAccExt.setDrawCircles(false);
        dataSetAccExt.setDrawValues(false);

        dataSetPressureInt.setValueTextColor(Color.WHITE);
        dataSetPressureInt.setDrawCircles(false);
        dataSetPressureInt.setDrawValues(false);
        dataSetPressureExt.setValueTextColor(Color.WHITE);
        dataSetPressureExt.setDrawCircles(false);
        dataSetPressureExt.setDrawValues(false);

        dataSetLightInt.setValueTextColor(Color.WHITE);
        dataSetLightInt.setDrawCircles(false);
        dataSetLightInt.setDrawValues(false);
        dataSetLightExt.setValueTextColor(Color.WHITE);
        dataSetLightExt.setDrawCircles(false);
        dataSetLightExt.setDrawValues(false);

        dataSetTempInt.setValueTextColor(Color.WHITE);
        dataSetTempInt.setDrawCircles(false);
        dataSetTempInt.setDrawValues(false);
        dataSetTempExt.setValueTextColor(Color.WHITE);
        dataSetTempExt.setDrawCircles(false);
        dataSetTempExt.setDrawValues(false);

        System.out.println("Should update the list here.");
        chart.invalidate();
    }

}
