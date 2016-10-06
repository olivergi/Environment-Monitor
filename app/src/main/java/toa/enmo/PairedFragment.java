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

public class PairedFragment extends Fragment {

    TextView tempText;
    TextView stepText;
    TextView pressureText;
    TextView lightText;
    Integer steps;
    String temperature;
    String pressure;
    String light;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.paired_fragment, container, false);
        steps = 0;

        stepText = (TextView) v.findViewById(R.id.pairedAccelText);
        tempText = (TextView) v.findViewById(R.id.pairedTempText);
        pressureText = (TextView) v.findViewById(R.id.pairedPressureText);
        lightText = (TextView) v.findViewById(R.id.pairedLightText);

        stepTest();
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
                stepText.setText(Integer.toString(steps));
                v.invalidate();
            }
        }, 300);

        return v;
    }

    private void stepTest() {
        try {
            final Bmi160Accelerometer bmi160AccModule = getBC().mwBoard.getModule(Bmi160Accelerometer.class);

            bmi160AccModule.enableStepDetection();
            System.out.println("Enabled Step Detector");

            bmi160AccModule.configureAxisSampling()
                    .setFullScaleRange(Bmi160Accelerometer.AccRange.AR_16G)
                    .setOutputDataRate(Bmi160Accelerometer.OutputDataRate.ODR_100_HZ)
                    .commit();
            System.out.println("Configure Step Detector");

            bmi160AccModule.enableAxisSampling();

            bmi160AccModule.enableStepDetection();

            bmi160AccModule.configureStepDetection()
                    // Set sensitivity to normal
                    .setSensitivity(Bmi160Accelerometer.StepSensitivity.NORMAL)
                    // Enable step counter
                    .enableStepCounter()
                    .commit();

            bmi160AccModule.start();
            System.out.println("Start Step Detector");

            bmi160AccModule.routeData().fromStepCounter(false).stream("step_counter").commit()
                    .onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe("step_counter", new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    Log.i("MainActivity", "Steps= " + msg.getData(Integer.class));
                                }
                            });
                            bmi160AccModule.readStepCounter(false);
                        }
                    });
            bmi160AccModule.routeData().fromStepDetection().stream("step_detector").commit()
                    .onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe("step_detector", new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    Log.i("MainActivity", "You took a step");
                                    steps = steps +1;
                                }
                            });
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
                                }
                            });
                            ltr329Module.start();
                        }
                    });
        } catch (UnsupportedModuleException e) {
            Log.e("MainActivity", "Module not present", e);
        }
    }

    private BluetoothControl getBC() {
        return ((MainActivity) getActivity()).bc;
    }
}