package toa.enmo;
/**
 * Created by Teemu on 27.9.2016.
 */
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;


final class SensorControl implements SensorEventListener, Runnable {
    private SensorManager sm;
    private Sensor accel;
    private Sensor ambientTemp;
    private Sensor gravity;
    private Sensor linAcc;
    private Sensor light;
    private Sensor humidity;
    private Sensor magnetic;
    private Sensor pressure;
    private Sensor gyroscope;
    private Context sensorContext;
    private DeviceFragment dfragment;

    SensorControl(Context context, DeviceFragment df){
        sensorContext = context;
        dfragment = df;
    }

    @Override
    public void run() {
        sm = (SensorManager) sensorContext.getSystemService(Context.SENSOR_SERVICE);

        if(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            System.out.println("No ACCELEOMETER Sensor");
        }
        if(sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            ambientTemp = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else {
            System.out.println("NO AMBIENT TEMP SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            gravity = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        } else {
            System.out.println("NO GRAVITY SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            linAcc = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        } else {
            System.out.println("NO LINEAR ACCELERATION SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        } else {
            System.out.println("NO LIGHT SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null){
            light = sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        } else {
            System.out.println("NO HUMIDITY SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            magnetic = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        } else{
            System.out.println("NO MAGNETIC FIELD SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            pressure = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else {
            System.out.println("NO PRESSURE SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            gyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        } else{
            System.out.println("NO GYROSCOPE SENSOR");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if (dfragment.accelText != null) {
                    float accValue = event.values[0];
                    dfragment.accelText.setText(Float.toString(accValue));
                }
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                if (dfragment.tempText != null) {
                    float tempValue = event.values[0];
                    dfragment.tempText.setText(Float.toString(tempValue) + " °C");
                }
                break;
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int i) {
        if(dfragment.isVisible()){
            dfragment.accText.setText(i == SensorManager.SENSOR_STATUS_ACCURACY_HIGH ? "HIGH" :
                    (i == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ? "MEDIUM" :
                            (i == SensorManager.SENSOR_STATUS_ACCURACY_LOW ? "LOW" : "UNRELIABLE")));
        }
    }
    public void register(){
        if(ambientTemp != null){
            sm.registerListener(this, ambientTemp,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            System.out.println("THERE IS NO AMBIENT TEMPERATURE SENSOR");
        }
        if(accel != null) {
            sm.registerListener(this, accel,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            System.out.println("THERE IS NO ACCELERATION SENSOR");
        }
    }

    public void unregister(){
        sm.unregisterListener(this);
    }
}

