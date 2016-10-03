package toa.enmo;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
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
import static com.mbientlab.metawear.MetaWearBoard.ConnectionStateHandler;

import android.os.IBinder;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private MetaWearBleService.LocalBinder serviceBinder;
    private final String MW_MAC_ADDRESS= "ED:17:4E:0A:7C:2E";
    private MetaWearBoard mwBoard;
    SensorControl sc;
    HomeScreenFragment hsf = new HomeScreenFragment();
    ConnectFragment cf = new ConnectFragment();
    AnalysisFragment af = new AnalysisFragment();
    DeviceFragment df = new DeviceFragment();
    PairedFragment pf = new PairedFragment();
    BluetoothAdapter BA;
    ProgressDialog mProgressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sc = new SensorControl(this, df);
        sc.run();

        // Bind the Metawear Service
        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
                this, Context.BIND_AUTO_CREATE);

        // Assign the bluetooth adapter and register the broadcast receiver
        BA = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        // UI Method to display the Home Screen Fragment
        changeFragment("hsf");

        // If Bluetooth is not enabled, prompt the device to turn on
        if (!BA.isEnabled()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }

        mProgressDlg = new ProgressDialog(this);

        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDlg.dismiss();

                BA.cancelDiscovery();
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
                BA.startDiscovery();
                // Show the progress dialog
                mProgressDlg.show();
                // Create a timer to dismiss the dialog
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mProgressDlg.dismiss();
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
                    System.out.println(device.getName() + "        " + device.getAddress());
                } else {
                    cf.theList.add("Device");
                }
                // Add the address to an array to use when trying to pair
                try {
                    cf.bluetoothDevices.add(device);

                } catch (Exception e) {
                    System.out.println("what: " + e);
                }
                // Update the array
                if (cf.isVisible()) {
                    ((ArrayAdapter) cf.lv.getAdapter()).notifyDataSetChanged();
                }
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                toaster("Bond State Checked");
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    toaster("Paired");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    toaster("Unpaired");
                }
            }

        }
    };

    // Do not delete yet, may be needed

   /* private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    toaster("Paired");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    toaster("Unpaired");
                }
            }
        }
    };*/

    public void toaster(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    public void retrieveBoard() {
        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard = serviceBinder.getMetaWearBoard(remoteDevice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sc.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sc.unregister();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
        sc.unregister();
        super.onDestroy();
        getApplicationContext().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (MetaWearBleService.LocalBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    private final ConnectionStateHandler stateHandler= new ConnectionStateHandler() {
        @Override
        public void connected() {
            Log.i("MainActivity", "Connected");
        }

        @Override
        public void disconnected() {
            Log.i("MainActivity", "Connected Lost");
        }

        @Override
        public void failure(int status, Throwable error) {
            Log.e("MainActivity", "Error connecting", error);
        }
    };

    public void connectBoard() {
        mwBoard.setConnectionStateHandler(stateHandler);
        mwBoard.connect();
    }
}
