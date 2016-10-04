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

import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import static com.mbientlab.metawear.MetaWearBoard.ConnectionStateHandler;

/**
 * Created by arttu on 10/3/16.
 */

public class BluetoothControl implements ServiceConnection, Runnable {
    private Context activityContext;
    private PairedFragment pFrag;
    private ConnectFragment cFrag;
    private MetaWearBleService.LocalBinder serviceBinder;
    private MetaWearBoard mwBoard;
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

    public void run() {
        connectBoard();
    }

    public void connectBoard() {
        mwBoard.setConnectionStateHandler(stateHandler);
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
        } else {
            System.out.println("This fucker is null");
        }
    }

    private final ConnectionStateHandler stateHandler = new ConnectionStateHandler() {
        @Override
        public void connected() {
            Log.i("MainActivity", "Connected");
            cFrag.isDeviceConnected = true;
            Looper.prepare();
            cFrag.toaster("Connected to " + cFrag.connectedDevice.getName());

        }

        @Override
        public void disconnected() {
            Log.i("MainActivity", "Connected Lost");
            cFrag.isDeviceConnected = false;
            cFrag.connectedDevice = null;

        }

        @Override
        public void failure(int status, Throwable error) {
            Log.e("MainActivity", "Error connecting", error);
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
}
