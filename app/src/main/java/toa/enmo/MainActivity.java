package toa.enmo;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorControl sc = new SensorControl();
    HomeScreenFragment hsf = new HomeScreenFragment();
    ConnectFragment cf = new ConnectFragment();
    AnalysisFragment af = new AnalysisFragment();
    DeviceFragment df = new DeviceFragment();
    PairedFragment pf = new PairedFragment();
    private SensorManager sm;
    private Sensor accel;
    private Sensor ambientTemp;
    boolean registerChecker = false;

    BluetoothAdapter BA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cf.theList = new ArrayList();
        BA = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        changeFragment("hsf");

        if (!BA.isEnabled()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            ambientTemp = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else {
            toaster("No Temperature Sensor");
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn1:
                changeFragment("df");
                break;
            case R.id.btn2:
                changeFragment("cf");
                break;
            case R.id.btn3:
                changeFragment("af");
                break;
            case R.id.btn4:
                changeFragment("pf");
                break;
        }
    }

    public void changeFragment (String fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (fragment){
            case "hsf":
                // Home Screen Fragment
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, hsf);
                BA.startDiscovery();
                break;
            case "hsfLeft":
                // Home Screen Fragment (Left Animation)
                transaction.setCustomAnimations(R.anim.left_enter, R.anim.left_exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, hsf);
                break;
            case "cf":
                // Connection Fragment
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, cf);
                BA.startDiscovery();
                break;
            case "af":
                // Analysis Fragment
                transaction.setCustomAnimations(R.anim.left_enter, R.anim.left_exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, af);
                break;
            case "df":
                // Device Fragment
                transaction.setCustomAnimations(R.anim.left_enter, R.anim.left_exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, df);
                break;
            case "pf":
                // Paired Fragment
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, pf);
                break;
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!hsf.isVisible()){
            if (af.isVisible() || df.isVisible()) {
                changeFragment("hsf");
            } else {
                changeFragment("hsfLeft");
            }
        } else {
            this.finish();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (device.getName() != null) {
                    cf.theList.add(device.getName());
                } else {
                    cf.theList.add("Device");
                }
                if (cf.isVisible()) {
                    ((ArrayAdapter) cf.lv.getAdapter()).notifyDataSetChanged();
                }
            }
        }
    };

    public void toaster(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int i) {
        if(df.isVisible()){
            df.accText.setText(i == SensorManager.SENSOR_STATUS_ACCURACY_HIGH ? "HIGH" :
                    (i == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ? "MEDIUM" :
                            (i == SensorManager.SENSOR_STATUS_ACCURACY_LOW ? "LOW" : "UNRELIABLE")));
        }
    }

    // Sensor Check Method
    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(df.isVisible()){
            switch(event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    float values = event.values[0];
                    df.accelText.setText(Float.toString(values));
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    float tempValue = event.values[0];
                    df.tempText.setText(Float.toString(tempValue) + " °C");
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    float humValues = event.values[0];
                    df.tempText.setText(Float.toString(humValues));
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void register(){
        sm.registerListener(this, ambientTemp,
                SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
