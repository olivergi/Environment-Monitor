package toa.enmo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MetaFragment extends Fragment {

    TextView tempText;
    TextView accelText;
    TextView pressureText;
    TextView lightText;
    String temperature;
    String pressure;
    String light;
    String acceleration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.paired_fragment, container, false);

        accelText = (TextView) v.findViewById(R.id.pairedAccelText);
        tempText = (TextView) v.findViewById(R.id.pairedTempText);
        pressureText = (TextView) v.findViewById(R.id.pairedPressureText);
        lightText = (TextView) v.findViewById(R.id.pairedLightText);

        temperature = getBC().temperature;
        pressure = getBC().pressure;
        light = getBC().light;
        acceleration = getBC().acceleration;

        // A timer with a delay, that sets the sensor values after they are fetched
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tempText.setText(temperature);
                pressureText.setText(pressure);
                lightText.setText(light);
                accelText.setText(acceleration);
                v.invalidate();
            }
        }, 300);

        return v;
    }

    /**
     * Method for updating the view about the sensor values
     *
     * @param msg    What sensor data is acquired
     * @param sensor To what text field the data is inserted
     */
    public void sensorMsg(String msg, final String sensor) {
        final String reading = msg;
        getMC().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (sensor) {
                    case "temp":
                        tempText.setText(reading);
                        break;
                    case "pres":
                        pressureText.setText(reading);
                        break;
                    case "light":
                        lightText.setText(reading);
                        break;
                    case "accel":
                        accelText.setText(reading);
                        break;
                }
            }
        });
    }

    private MainActivity getMC() {
        return ((MainActivity) getActivity());
    }

    private BluetoothControl getBC() {
        return ((MainActivity) getActivity()).bc;
    }
}