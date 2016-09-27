package toa.enmo;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SensorControl sc = new SensorControl();
    HomeScreenFragment hsf = new HomeScreenFragment();
    ConnectFragment cf = new ConnectFragment();
    AnalysisFragment af = new AnalysisFragment();
    DeviceFragment df = new DeviceFragment();
    PairedFragment pf = new PairedFragment();

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
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        BA.startDiscovery();

        changeFragment("hsf");
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
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, hsf);
                break;
            case "hsfLeft":
                transaction.setCustomAnimations(R.anim.left_enter, R.anim.left_exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, hsf);
                break;
            case "cf":
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, cf);
                break;
            case "af":
                transaction.setCustomAnimations(R.anim.left_enter, R.anim.left_exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, af);
                break;
            case "df":
                transaction.setCustomAnimations(R.anim.left_enter, R.anim.left_exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, df);
                break;
            case "pf":
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
    protected void onResume() {
        super.onResume();
        //sc.sm.registerListener(sc, sc.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sc.sm.unregisterListener(sc);
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
}
