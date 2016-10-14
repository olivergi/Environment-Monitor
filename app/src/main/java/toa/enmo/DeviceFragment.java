package toa.enmo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeviceFragment extends Fragment {

    TextView tempText;
    TextView accelText;
    TextView pressureText;
    TextView lightText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.device_fragment, container, false);
        accelText = (TextView) v.findViewById(R.id.accelText);
        tempText = (TextView) v.findViewById(R.id.temperatureText);
        pressureText = (TextView) v.findViewById(R.id.pressureText);
        lightText = (TextView) v.findViewById(R.id.lightText);

        return v;
    }
}
