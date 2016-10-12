package toa.enmo;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.*;

/**
 * Created by iosdev on 22.9.2016.
 */

public class AnalysisFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.analysis_fragment, container, false);
        LineChart chart = (LineChart) v.findViewById(R.id.chart);

        return v;
    }
    private SensorControl getSC(){
        return ((MainActivity)getActivity()).sc;
    }

}
