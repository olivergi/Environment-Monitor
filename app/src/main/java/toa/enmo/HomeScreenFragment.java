package toa.enmo;

import android.app.FragmentTransaction;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeScreenFragment extends Fragment {

    AnalysisFragment af;

    public HomeScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        Button button = (Button) v.findViewById(R.id.btn3);
        af = new AnalysisFragment();

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentTransaction fm = getFragmentManager().beginTransaction();
                fm.replace(R.id.frag_container, af);
                fm.commit();
            }
        });

        return v;
    }
}