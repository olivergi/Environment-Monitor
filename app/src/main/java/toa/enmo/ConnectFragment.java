package toa.enmo;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ConnectFragment extends Fragment {

    ArrayList theList;
    ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.connect_fragment, container, false);
        lv = (ListView) v.findViewById(R.id.listView);

        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, theList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, final int position, long arg3) {
                final String value = (String)adapter.getItemAtPosition(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Connect to this device?");
                adb.setMessage("Hello");
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // connect to the device
                        System.out.println("making connection");
                        BluetoothDevice device = bluetoothDevices.get(position);

                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            System.out.println("unpairing device");
                            unpairDevice(device);
                        } else {
                            System.out.println("pairing device");
                            pairDevice(device);
                        }
                    }
                });
                adb.setNegativeButton("No", null);
                adb.show();
            }
        });

        return v;
    }


    public void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            System.out.println("pair error: " + e);
        }
    }

    public void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            System.out.println("unpair error: " + e);
        }
    }
}
