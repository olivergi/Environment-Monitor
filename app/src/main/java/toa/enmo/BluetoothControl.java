package toa.enmo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import static com.mbientlab.metawear.MetaWearBoard.ConnectionStateHandler;

/**
 * Created by arttu on 10/3/16.
 */

public class BluetoothControl extends Thread implements ServiceConnection {
    private Context activityContext;
    MainActivity context = (MainActivity)activityContext;
    private PairedFragment pFrag;
    private ConnectFragment cFrag;
    private MetaWearBleService.LocalBinder serviceBinder;
    private MetaWearBoard mwBoard;


    public BluetoothControl (Context c, PairedFragment f, ConnectFragment cf) {
        activityContext = c;
        pFrag = f;
        cFrag = cf;
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
            cFrag.deviceConnected = true;
        }

        @Override
        public void disconnected() {
            Log.i("MainActivity", "Connected Lost");
            cFrag.deviceConnected = false;
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
}
