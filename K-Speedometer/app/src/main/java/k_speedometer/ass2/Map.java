package k_speedometer.ass2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import org.w3c.dom.Text;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Map extends Activity implements LocationListener, View.OnClickListener {

    private GoogleMap map;
    private Button bStartStop, btnViewHistory;
    private TextView tvSpeedTime;
    private String speed, time, timeToStore;
    SQLite objSql;

    private long startTime = 0L;
    long timeMSec = 0L;

    private Handler handler = new Handler();

    int secs, mins, hrs;
    float currentspeed, maxSpeed;
    private String speed_txt, time_txt, error, done, failed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        speed_txt = getResources().getString(R.string.speed_info);
        time_txt = getResources().getString(R.string.time_info);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        bStartStop = (Button) findViewById(R.id.bStartStop);
        bStartStop.setText("Start");
        tvSpeedTime = (TextView) findViewById(R.id.tvSpeedTime);
        btnViewHistory = (Button) findViewById(R.id.btnHistory);
        currentspeed = 0;
        maxSpeed = 0;
        speed = currentspeed + " km/h";
        time = "00:00:00";
        tvSpeedTime.setText(speed_txt + " " + speed + "\n\n" + time_txt + " " + time);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.onLocationChanged(null);

        bStartStop.setOnClickListener(this);
        btnViewHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        speed_txt = getResources().getString(R.string.speed_info);
        time_txt = getResources().getString(R.string.time_info);
        switch (view.getId()) {
            case R.id.bStartStop:
                if (bStartStop.getText().toString().equals("Start")) {
                    bStartStop.setText("Stop");
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimer, 0);
                } else {
                    timeToStore = time;
                    bStartStop.setText("Start");
                    secs = mins = hrs = 0;
                    time = String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
                    currentspeed = 0;
                    speed = currentspeed + " km/h";
                    tvSpeedTime.setText(speed_txt + " " + speed + "\n\n" + time_txt + " " + time);
                    handler.removeCallbacks(updateTimer);
                    RegisterInData();
                }
                break;
            case R.id.btnHistory:
                Intent i = new Intent("android.intent.action.viewHistory");
                startActivity(i);
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
            tvSpeedTime.setText(speed_txt + " " + speed + "\n\n" + time_txt + " " + time);
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && bStartStop.getText().toString().equals("Stop")) {
            currentspeed = location.getSpeed() * 36 / 10;
            CompareSpeed((int) currentspeed);
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

    private void CompareSpeed(int speed) {
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
    }

    public void RegisterInData() {

        done = getResources().getString(R.string.data_registred_info);
        failed = getResources().getString(R.string.data_failed);
        error = getResources().getString(R.string.error_info);
        objSql = new SQLite(this);
        try {
            objSql.Open();
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            if (maxSpeed != 0) {
                String SpeedToInsert = String.valueOf(maxSpeed);
                objSql.InsertData(date, SpeedToInsert);
                alert.setMessage("Max. " + speed_txt + " " + maxSpeed + "km/h\n" + time_txt + " " + timeToStore + "\n" + done);
            }else{
                alert.setMessage("Max. " + speed_txt + " " + maxSpeed + "km/h\n" + time_txt + " " + timeToStore);
            }
            alert.setTitle("Info!");
            alert.setPositiveButton("OK", null);
            alert.show();
            maxSpeed = 0;
        } catch (SQLException e) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(error);
            alert.setMessage(failed);
            alert.setPositiveButton("OK", null);
            alert.show();
        } finally {
            objSql.Close();
        }


    }

}
