package toa.enmo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ConnectFragment extends Fragment {

    ArrayList theList;
    ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.connect_fragment, container, false);
        lv = (ListView) v.findViewById(R.id.listView);

        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, theList);
        lv.setAdapter(adapter);
        
        return v;
    }
}
