package toa.enmo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    SensorControl sc = new SensorControl();
    HomeScreenFragment hsf = new HomeScreenFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        System.out.println("HELLO");

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frag_container, hsf);
        ft.commit();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn1:
                System.out.println("hello");
                break;
        }
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
        if (hsf.isVisible()){
            System.out.println("Hello");
            this.finish();
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frag_container, hsf);
            ft.commit();
        }

    }
}
