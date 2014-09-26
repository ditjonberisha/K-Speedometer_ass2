package k_speedometer.ass2;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import org.w3c.dom.Text;


public class Map extends Activity implements LocationListener, View.OnClickListener {

    private GoogleMap map;
    private Button bStartStop;
    private TextView tvSpeedTime;
    private String speed, time;

    private long startTime = 0L;
    long timeMSec = 0L;

    private Handler handler = new Handler();

    int secs, mins, hrs;
    float currentspeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        bStartStop = (Button) findViewById(R.id.bStartStop);
        bStartStop.setText("Start");
        tvSpeedTime = (TextView) findViewById(R.id.tvSpeedTime);

        currentspeed = 0;
        speed = currentspeed + " km/h";
        time = "00:00:00";
        tvSpeedTime.setText("Speed : " + speed + "\n\nTime : " + time);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.onLocationChanged(null);

        bStartStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bStartStop:
                if (bStartStop.getText().toString() == "Start") {
                    bStartStop.setText("Stop");

                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimer, 0);
                } else {
                    bStartStop.setText("Start");
                    secs = mins = hrs = 0;
                    time = String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
                    currentspeed = 0;
                    speed = currentspeed + " km/h";
                    tvSpeedTime.setText("Speed : " + speed + "\n\nTime : " + time);
                    handler.removeCallbacks(updateTimer);
                }
                break;
        }
    }

    private Runnable updateTimer = new Runnable() {

        public void run() {

            timeMSec = SystemClock.uptimeMillis() - startTime;

            secs = (int) (timeMSec / 1000);
            mins = secs / 60;
            hrs = secs / 3600;
            secs = secs % 60;
            time = String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
            tvSpeedTime.setText("Speed : " + speed + "\n\nTime : " + time);
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        if(location != null && bStartStop.getText().toString() == "Stop") {
            currentspeed = location.getSpeed() * 36 / 10;
            speed = currentspeed + " km/h";
            time = String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
