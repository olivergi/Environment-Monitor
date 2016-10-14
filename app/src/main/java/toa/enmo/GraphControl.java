package toa.enmo;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by arttu on 10/13/16.
 */

public class GraphControl {

    ArrayList<Entry> entriesAccInt = new ArrayList<>();
    ArrayList<Entry> entriesAccExt = new ArrayList<>();
    ArrayList<Entry> entriesPresInt = new ArrayList<>();
    ArrayList<Entry> entriesPresExt = new ArrayList<>();
    ArrayList<Entry> entriesLightInt = new ArrayList<>();
    ArrayList<Entry> entriesLightExt = new ArrayList<>();
    ArrayList<Entry> entriesTempInt = new ArrayList<>();
    ArrayList<Entry> entriesTempExt = new ArrayList<>();

    LineDataSet dataSetAccInt;
    LineDataSet dataSetAccExt;
    LineDataSet dataSetTempInt;
    LineDataSet dataSetTempExt;
    LineDataSet dataSetLightInt;
    LineDataSet dataSetLightExt;
    LineDataSet dataSetPressureInt;
    LineDataSet dataSetPressureExt;

    int counter = 0;
    LineData accData;
    LineData pressData;
    LineData lightData;
    LineData tempData;

    private Context activityContext;
    private AnalysisFragment af;


    public GraphControl(Context context, AnalysisFragment af) {
        this.activityContext = context;
        this.af = af;
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

    public void createDataShit() {
        entriesAccInt.add(new Entry(counter , getSC().acceleration));
        entriesAccExt.add(new Entry(counter , getBC().accValue));
        entriesPresInt.add(new Entry(counter , getSC().pressure));
        entriesPresExt.add(new Entry(counter , getBC().pressValue));
        entriesLightInt.add(new Entry(counter , getSC().light));
        entriesLightExt.add(new Entry(counter , getBC().lightValue));
        entriesTempInt.add(new Entry(counter , getSC().temperature));
        entriesTempExt.add(new Entry(counter , getBC().tempValue));
        counter = counter+2;

        dataSetAccInt = new LineDataSet(entriesAccInt, "Internal Acceleration");
        dataSetAccInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetAccInt.setColor(Color.MAGENTA);
        dataSetAccInt.setDrawCircles(false);
        dataSetAccInt.setDrawValues(false);

        dataSetAccExt = new LineDataSet(entriesAccExt, "External Acceleration");
        dataSetAccExt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetAccExt.setColor(Color.GREEN);
        dataSetAccExt.setDrawCircles(false);
        dataSetAccExt.setDrawValues(false);

        dataSetPressureInt = new LineDataSet(entriesPresInt, "Internal Pressure");
        dataSetPressureInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetPressureInt.setColor(Color.MAGENTA);
        dataSetPressureInt.setDrawCircles(false);
        dataSetPressureInt.setDrawValues(false);

        dataSetPressureExt = new LineDataSet(entriesPresExt, "External Pressure");
        dataSetPressureExt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetPressureExt.setColor(Color.GREEN);
        dataSetPressureExt.setDrawCircles(false);
        dataSetPressureExt.setDrawValues(false);

        dataSetLightInt = new LineDataSet(entriesLightInt, "Internal Lux");
        dataSetLightInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetLightInt.setColor(Color.MAGENTA);
        dataSetLightInt.setDrawCircles(false);
        dataSetLightInt.setDrawValues(false);

        dataSetLightExt = new LineDataSet(entriesLightExt, "External Lux");
        dataSetLightExt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetLightExt.setColor(Color.GREEN);
        dataSetLightExt.setDrawCircles(false);
        dataSetLightExt.setDrawValues(false);

        dataSetTempInt = new LineDataSet(entriesTempInt, "Internal Temperature");
        dataSetTempInt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetTempInt.setColor(Color.MAGENTA);
        dataSetTempInt.setDrawCircles(false);
        dataSetTempInt.setDrawValues(false);

        dataSetTempExt = new LineDataSet(entriesTempExt, "External Temperature");
        dataSetTempExt.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetTempExt.setColor(Color.GREEN);
        dataSetTempExt.setDrawCircles(false);
        dataSetTempExt.setValueTextColor(Color.WHITE);
        dataSetTempExt.setDrawValues(false);

        ArrayList<ILineDataSet> accDataSets = new ArrayList<>();
        accDataSets.add(dataSetAccInt);
        accDataSets.add(dataSetAccExt);

        ArrayList<ILineDataSet> pressDataSets = new ArrayList<>();
        pressDataSets.add(dataSetPressureInt);
        pressDataSets.add(dataSetPressureExt);

        ArrayList<ILineDataSet> lightDataSets = new ArrayList<>();
        lightDataSets.add(dataSetLightInt);
        lightDataSets.add(dataSetLightExt);

        ArrayList<ILineDataSet> tempDataSets = new ArrayList<>();
        tempDataSets.add(dataSetTempInt);
        tempDataSets.add(dataSetTempExt);

        accData = new LineData(accDataSets);
        pressData = new LineData(pressDataSets);
        lightData = new LineData(lightDataSets);
        tempData = new LineData(tempDataSets);
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

        //af.chart.notifyDataSetChanged();
        // xuaf.chart.invalidate();
    }


    private SensorControl getSC() {
        return ((MainActivity) activityContext).sc;
    }

    private BluetoothControl getBC() {
        return ((MainActivity) activityContext).bc;
    }
}
