package toa.enmo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.data.CartesianFloat;
import com.mbientlab.metawear.module.Bme280Humidity;
import com.mbientlab.metawear.module.Bmi160Accelerometer;
import com.mbientlab.metawear.module.MultiChannelTemperature;
import com.mbientlab.metawear.module.MultiChannelTemperature.ExtThermistor;
import com.mbientlab.metawear.module.Bmp280Barometer;
import com.mbientlab.metawear.module.Bmp280Barometer.*;
import com.mbientlab.metawear.module.Ltr329AmbientLight;
import com.mbientlab.metawear.module.Ltr329AmbientLight.*;

import java.util.List;

/**
 * Created by iosdev on 23.9.2016.
 */

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

        acceleration();
        temperature();
        pressure();
        light();

        // A timer with a delay, that sets the temperature after it's fetched
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

    private void acceleration() {
        try {
            final Bmi160Accelerometer accModule = getBC().mwBoard.getModule(Bmi160Accelerometer.class);

            // Set measurement range to +/- 16G
            // Set output data rate to 100Hz
            accModule.configureAxisSampling()
                    .setFullScaleRange(Bmi160Accelerometer.AccRange.AR_16G)
                    .setOutputDataRate(Bmi160Accelerometer.OutputDataRate.ODR_100_HZ)
                    .commit();
            // enable axis sampling
            accModule.enableAxisSampling();

            accModule.routeData().fromHighFreqAxes().stream("high_freq").commit()
                    .onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe("high_freq", new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    Log.i("test", "high freq: " + msg.getData(CartesianFloat.class));
                                    acceleration = (msg.getData(Float.class).toString());
                                    sensorMsg(String.format(acceleration), "accel");
                                }
                            });

                            accModule.setOutputDataRate(200.f);
                            accModule.enableAxisSampling();
                            accModule.start();
                        }
                    });

        } catch (UnsupportedModuleException e) {
            Log.e("MainActivity", "Module not present", e);
        }

    }

    private void temperature() {
        try {
            final MultiChannelTemperature mcTempModule = getBC().mwBoard.getModule(MultiChannelTemperature.class);
            final List<MultiChannelTemperature.Source> tempSources = mcTempModule.getSources();

            mcTempModule.routeData()
                    .fromSource(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE)).stream("temp_nrf_stream")
                    .commit().onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                @Override
                public void success(RouteManager result) {
                    result.subscribe("temp_nrf_stream", new RouteManager.MessageHandler() {
                        @Override
                        public void process(Message msg) {
                            temperature = (msg.getData(Float.class).toString() + " °C");
                            sensorMsg(String.format(temperature), "temp");
                        }
                    });

                    // Read temperature from the NRF soc chip
                    mcTempModule.readTemperature(tempSources.get(MultiChannelTemperature.MetaWearRChannel.NRF_DIE));

                }
            });

        } catch (UnsupportedModuleException e) {
            Log.e("MainActivity", "Module not present", e);
        }

    }

    public void pressure() {
        try {

            final Bmp280Barometer bmp280Module = getBC().mwBoard.getModule(Bmp280Barometer.class);

            bmp280Module.configure()
                    .setFilterMode(FilterMode.AVG_4)
                    .setPressureOversampling(OversamplingMode.LOW_POWER)
                    .setStandbyTime(StandbyTime.TIME_125)
                    .commit();

            bmp280Module.routeData().fromPressure().stream("pressure_stream").commit()
                    .onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe("pressure_stream", new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    pressure = (msg.getData(Float.class).toString() + " Pa");
                                    sensorMsg(String.format(pressure), "pres");
                                }
                            });
                            bmp280Module.start();
                        }
                    });
        } catch (UnsupportedModuleException e) {
            Log.e("MainActivity", "Module not present", e);
        }
    }

    public void light() {
        try {
            final Ltr329AmbientLight ltr329Module = getBC().mwBoard.getModule(Ltr329AmbientLight.class);

            ltr329Module.configure().setGain(Gain.LTR329_GAIN_4X)
                    .setIntegrationTime(IntegrationTime.LTR329_TIME_150MS)
                    .setMeasurementRate(MeasurementRate.LTR329_RATE_100MS)
                    .commit();

            ltr329Module.routeData().fromSensor().stream("light_sub").commit()
                    .onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe("light_sub", new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    light = (msg.getData(Long.class).toString() + " lx");
                                    sensorMsg(String.format(light), "light");
                                }
                            });
                            ltr329Module.start();
                        }
                    });
        } catch (UnsupportedModuleException e) {
            Log.e("MainActivity", "Module not present", e);
        }
    }

    public void sensorMsg(String msg, final String sensor) {
        final String reading = msg;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (sensor) {
                    case "temp":
                        System.out.println("setting temp: " + reading);
                        tempText.setText(reading);
                        break;
                    case "pres":
                        System.out.println("setting press: " + reading);
                        pressureText.setText(reading);
                        break;
                    case "light":
                        System.out.println("setting light: " + reading);
                        lightText.setText(reading);
                        break;
                    case "accel":
                        System.out.println("setting accel: " + reading);
                        accelText.setText(reading);
                        break;
                }
            }
        });
    }

    private BluetoothControl getBC() {
        return ((MainActivity) getActivity()).bc;
    }
}