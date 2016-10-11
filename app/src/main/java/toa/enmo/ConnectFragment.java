package toa.enmo;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ConnectFragment extends Fragment {

    ArrayList theList;
    ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    ListView lv;
    Boolean isDeviceConnected = false;
    BluetoothDevice connectedDevice;
    int connectedDeviceIndex = 0;
    BluetoothDevice deviceItem;
    ProgressDialog connectDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.connect_fragment, container, false);
        lv = (ListView) v.findViewById(R.id.listView);

        createConnectDialog();

        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, theList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, final int position, long arg3) {
                AlertDialog.Builder adb = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
                deviceItem = bluetoothDevices.get(position);

                if (!isDeviceConnected && !(deviceItem == connectedDevice)) {
                    adb.setTitle("Connect to this device?");
                    adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // connect to the device
                            System.out.println("making connection");
                            connectDialog.show();

                            String address = deviceItem.getAddress();
                            // If there is not a connected device yet, retrieve and connect
                            getBC().retrieveBoard(address);
                            connectedDeviceIndex = position;
                            getBC().createConnection();
                        }
                    });
                } else {
                    adb.setTitle("You are already connected.");
                    adb.setMessage("There can be only one connection.\nDo you want to disconnect?");
                    adb.setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Disconnect from the device
                            System.out.println("Disconnecting");
                            if(getBC().ledModule != null){
                                getBC().ledModule.stop(true);
                            }
                            getBC().disconnectBoard();

                        }
                    });
                }
                adb.setNegativeButton("No", null);
                adb.show();
            }
        });
        return v;
    }

    private BluetoothControl getBC(){
       return ((MainActivity)getActivity()).bc;
    }

    public void createConnectDialog(){
        connectDialog = new ProgressDialog(getActivity());
        connectDialog.setMessage("Connecting...");
    }
}
