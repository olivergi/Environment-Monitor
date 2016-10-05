package toa.enmo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.data.CartesianFloat;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Bmi160Accelerometer;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.Switch;

import static com.mbientlab.metawear.MetaWearBoard.ConnectionStateHandler;

/**
 * Created by arttu on 10/3/16.
 */

public class BluetoothControl implements ServiceConnection {
    private Context activityContext;
    private PairedFragment pFrag;
    private ConnectFragment cFrag;
    private MetaWearBleService.LocalBinder serviceBinder;
    MetaWearBoard mwBoard;
    BluetoothAdapter BA;


    public BluetoothControl (Context c, PairedFragment f, ConnectFragment cf) {
        activityContext = c;
        pFrag = f;
        cFrag = cf;

        // Assign the bluetooth adapter and register the broadcast receiver
        BA = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activityContext.registerReceiver(mReceiver, filter);
    }


    public void toaster(String s) {
        Toast.makeText(activityContext, s, Toast.LENGTH_SHORT).show();
    }

    public void createConnection() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                System.out.println("thread running");
                    connectBoard();
                }

        });
        thread.start();
    }

    public void connectBoard() {
        mwBoard.connect();
    }

    public void disconnectBoard() {
        mwBoard.disconnect();
    }

    public void retrieveBoard(String address) {
        final BluetoothManager btManager =
                (BluetoothManager) activityContext.getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(address);

        System.out.println("Retrieving board\n " + "remotedevice: " +remoteDevice);

        if (remoteDevice != null) {
            // Create a MetaWear board object for the Bluetooth Device
            System.out.println("Remotedevice is alive");
            mwBoard = serviceBinder.getMetaWearBoard(remoteDevice);
            mwBoard.setConnectionStateHandler(stateHandler);
        } else {
            System.out.println("This fucker is null");
        }
    }

    private final ConnectionStateHandler stateHandler = new ConnectionStateHandler() {
        @Override
        public void connected() {
            Log.i("MainActivity", "Connected");
            cFrag.isDeviceConnected = true;
            cFrag.connectedDevice = cFrag.bluetoothDevices.get(cFrag.connectedDeviceIndex);

        }

        @Override
        public void disconnected() {
            Log.i("MainActivity", "Disconnected");
            cFrag.isDeviceConnected = false;
            cFrag.connectedDevice = null;

        }

        @Override
        public void failure(int status, Throwable error) {
            Log.e("MainActivity", "Error connecting", error);
            cFrag.isDeviceConnected = false;
            cFrag.connectedDevice = null;
        }
    };

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (MetaWearBleService.LocalBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

     BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Check that the name contains words that relate to the sensor
                String tempString;
                tempString = device.getName();
                if (tempString != null){
                    tempString = tempString.toLowerCase();

                    if (tempString.contains("wear") || tempString.contains("meta")){
                        cFrag.theList.add(device.getName());

                        // Add the address to an array to use when trying to pair
                        try {
                            cFrag.bluetoothDevices.add(device);

                        } catch (Exception e) {
                            System.out.println("what: " + e);
                        }
                    }
                }

                System.out.println(device.getName() + "        " + device.getAddress());
                // Update the array
                if (cFrag.isVisible()) {
                    ((ArrayAdapter) cFrag.lv.getAdapter()).notifyDataSetChanged();
                }
            }

        }
    };

    public void registerModule() {
        try {
            final Switch switchModule = mwBoard.getModule(Switch.class);
            final Led ledModule = mwBoard.getModule(Led.class);
            //accelModule = mwBoard.getModule(Accelerometer.class);

            switchModule.routeData().fromSensor();
        } catch (UnsupportedModuleException e) {
            Log.e("MainActivity", "Module not present", e);
        }
    }

}
