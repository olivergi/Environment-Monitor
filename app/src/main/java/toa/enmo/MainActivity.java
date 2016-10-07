package toa.enmo;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;

import android.os.IBinder;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    SensorControl sc;
    BluetoothControl bc;
    HomeScreenFragment hsf = new HomeScreenFragment();
    ConnectFragment cf = new ConnectFragment();
    AnalysisFragment af = new AnalysisFragment();
    DeviceFragment df = new DeviceFragment();
    PairedFragment pf = new PairedFragment();
    ProgressDialog mProgressDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sc = new SensorControl(this, df);
        bc = new BluetoothControl(this, pf, cf);
        sc.run();
        // Bind the Metawear Service
        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
                bc, Context.BIND_AUTO_CREATE);

        // UI Method to display the Home Screen Fragment
        changeFragment("hsf");

        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDlg.dismiss();
                bc.BA.cancelDiscovery();
            }
        });

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
                if (cf.connectedDevice != null){
                    changeFragment("pf");
                } else {
                    bc.toaster("No Device Connected");
                }
                break;
            case R.id.scanButton:
                cf.theList.clear();
                cf.bluetoothDevices.clear();
                ((ArrayAdapter) cf.lv.getAdapter()).notifyDataSetChanged();

                bc.BA.startDiscovery();
                mProgressDlg.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mProgressDlg.dismiss();
                        bc.BA.cancelDiscovery();
                    }
                }, 10000);
                break;
        }
    }

    public void checkBluetooth() {
        if (!bc.BA.isEnabled()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }
    }

    public void blueToothAlert() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Disconnect from " + cf.connectedDevice.getName() + "?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bc.disconnectBoard();
            }
        });

        adb.setNegativeButton("No", null);
        adb.show();

    }

    public void exitAlert() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Do you wish to exit?");
        if (cf.isDeviceConnected){
            adb.setMessage("Exiting will disconnect you from " + cf.connectedDevice.getName() + ".");
        }
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        adb.setNegativeButton("No", null);
        adb.show();

    }

    public void changeFragment (String fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (fragment){
            case "hsf":
                // Home Screen Fragment
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.replace(R.id.frag_container, hsf);
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
                cf.theList = new ArrayList();
                bc.BA.startDiscovery();
                // Show the progress dialog
                mProgressDlg.show();
                // Create a timer to dismiss the dialog
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mProgressDlg.dismiss();
                        bc.BA.cancelDiscovery();
                    }
                }, 10000);
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
        invalidateOptionsMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem connectionState = menu.findItem(R.id.action_connection);
        connectionState.setVisible(false);
        if(bc.cFrag.connectedDevice != null){
            connectionState.setVisible(true);
        } else {
            if(connectionState.isVisible()){
                connectionState.setVisible(false);
            }
        }
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
        if (id == R.id.action_connection){
            blueToothAlert();
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
            exitAlert();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        sc.register();
        checkBluetooth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        invalidateOptionsMenu();
        sc.unregister();
    }

    @Override
    public void onDestroy(){
        if (cf.connectedDevice != null){
            bc.disconnectBoard();
        }
        unregisterReceiver(bc.mReceiver);
        sc.unregister();
        super.onDestroy();
        getApplicationContext().unbindService(bc);
    }

}