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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;


final class SensorControl implements SensorEventListener, Runnable {
    private SensorManager sm;
    private Sensor accel;
    private Sensor ambientTemp;
    private Sensor gravity;
    private Sensor linAcc;
    private Sensor lightSensor;
    private Sensor humidity;
    private Sensor magnetic;
    private Sensor pressureSensor;
    private Sensor gyroscope;
    private Context sensorContext;
    private DeviceFragment dfragment;

    public float acceleration;
    public float temperature;
    public float pressure;
    public float light;

    public SensorControl(Context context, DeviceFragment df){
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
            lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        } else {
            System.out.println("NO LIGHT SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null){
            lightSensor = sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        } else {
            System.out.println("NO HUMIDITY SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            magnetic = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        } else {
            System.out.println("NO MAGNETIC FIELD SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            pressureSensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else {
            System.out.println("NO PRESSURE SENSOR");
        }

        if(sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            gyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        } else {
            System.out.println("NO GYROSCOPE SENSOR");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                float accValue = event.values[0];
                float accValueTwo = event.values[1];
                float accValueThree = event.values[2];
                acceleration = accValueTwo;
                    if(accValue > 0 && dfragment.accelText != null) {
                        dfragment.accelText.setText(round(accValue, 2) + ", " + round(accValueTwo, 2) + ", " + round(accValueThree, 2) + " m/s²");
                    }
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                float tempValue = event.values[0];
                temperature = tempValue;
                if (dfragment.tempText != null) {
                    dfragment.tempText.setText(round(tempValue, 2) + " °C");
                }
                break;
            case Sensor.TYPE_PRESSURE:
                float pressValue = event.values[0];
                pressure = pressValue;
                if (dfragment.pressureText != null) {
                    dfragment.pressureText.setText(round(pressValue, 2) + " mBar");
                }
                break;
            case Sensor.TYPE_LIGHT:
                float lightValue = event.values[0];
                light = lightValue;
                if (dfragment.lightText != null) {
                    dfragment.lightText.setText(round(lightValue, 2) + " lux");
                }
                break;
        }
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int i) {
        /*if(dfragment.isVisible()){
            dfragment.accText.setText(i == SensorManager.SENSOR_STATUS_ACCURACY_HIGH ? "HIGH" :
                    (i == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ? "MEDIUM" :
                            (i == SensorManager.SENSOR_STATUS_ACCURACY_LOW ? "LOW" : "UNRELIABLE")));
        } */
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
        if (lightSensor != null) {
            sm.registerListener(this, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            System.out.println("THERE IS NO LIGHT SENSOR");
        }
        if (pressureSensor != null) {
            sm.registerListener(this, pressureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            System.out.println("THERE IS NO LIGHT SENSOR");
        }
    }

    public void unregister(){
        sm.unregisterListener(this);
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}

