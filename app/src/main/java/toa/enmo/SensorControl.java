package toa.enmo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Timer;


public class SensorControl {
    SensorManager sm;
    Sensor mSensor;
    Timer timer;


    public class TempSensor implements SensorEventListener, Runnable{

        @Override
        public void onSensorChanged(SensorEvent event) {
            // Things happen
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void run() {

        }
    }

}
