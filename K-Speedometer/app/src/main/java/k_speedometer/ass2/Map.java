package k_speedometer.ass2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/*
    Created by Arnold, Ditjon and Blend
 */

public class Map extends Activity implements LocationListener, View.OnClickListener {

    //Declaration of variables
    private GoogleMap map;
    private Button bStartStop;
    private Button btnViewHistory;
    private TextView tvSpeedTime;
    private String speed;
    private String time;
    private String timeToStore;
    private String speed_txt;
    private String time_txt;
    private String error;
    private String done;
    private String failed;
    private int secs;
    private int mins;
    private int hrs;
    private long startTime = 0L;
    private long timeMSec = 0L;
    private float currentspeed;
    private float maxSpeed;
    SQLite  objSql;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        this.Init();
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.onLocationChanged(null);
        bStartStop.setOnClickListener(this);
        btnViewHistory.setOnClickListener(this);
    }

    public void Init() {
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
        tvSpeedTime.setText(speed_txt + " " + speed + "\n" + time_txt + " " + time);
    }

    @Override
    public void onClick(View view) {
        speed_txt = getResources().getString(R.string.speed_info);
        time_txt = getResources().getString(R.string.time_info);
        switch (view.getId()) {
            case R.id.bStartStop:
                if (bStartStop.getText().toString().equals("Start")) {
                    bStartStop.setText("Stop");
                    startTime = SystemClock.uptimeMillis(); // Gets the system's current time
                    handler.postDelayed(updateTimer, 0); //Starts the thread for the timer
                } else {
                    //When the timer stops, time and speed will be set equal to zero
                    timeToStore = time;
                    bStartStop.setText("Start");
                    secs = mins = hrs = 0;
                    time = String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
                    currentspeed = 0;
                    speed = currentspeed + " km/h";
                    tvSpeedTime.setText(speed_txt + " " + speed + "\n" + time_txt + " " + time);
                    handler.removeCallbacks(updateTimer); //Stops the thread for the timer
                    RegisterInData(); //Registers the data to the database
                }
                break;
            case R.id.btnHistory:
                Intent i = new Intent("android.intent.action.viewHistory");
                startActivity(i);
        }
    }


    private Runnable updateTimer = new Runnable() {

        public void run() {

            timeMSec = SystemClock.uptimeMillis() - startTime; //Gets the time since the timer has started

            //Gets the seconds, minutes, and hours
            secs = (int) (timeMSec / 1000);
            mins = secs / 60;
            hrs = secs / 3600;
            secs = secs % 60;
            time = String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
            tvSpeedTime.setText(speed_txt + " " + speed + "\n" + time_txt + " " + time);
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && bStartStop.getText().toString().equals("Stop")) {
            currentspeed = location.getSpeed() * 36 / 10;
            CompareSpeed((int) currentspeed);
            speed = new DecimalFormat("#0.00").format(currentspeed) + " km/h";
            time = String.format("%02d", hrs) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {    }

    @Override
    public void onProviderEnabled(String s) {   }

    @Override
    public void onProviderDisabled(String s) {   }

    //Check if the current speed is greater than the maximal speed registered
    private void CompareSpeed(int speed) {
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
    }

    @SuppressLint("all")
    public void RegisterInData() {

        //Get text form string.xml
        done = getResources().getString(R.string.data_registred_info);
        failed = getResources().getString(R.string.data_failed);
        error = getResources().getString(R.string.error_info);
        objSql = new SQLite(this);
        try {
            objSql.Open();
            //Get today's date
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            //Is speed is not 0 insert in database
            if (maxSpeed != 0) {
                //Convert float to string
                String SpeedToInsert = String.valueOf(maxSpeed);
                objSql.InsertData(date, SpeedToInsert, timeToStore);
                alert.setMessage("Max. " + speed_txt + " " + maxSpeed + "km/h\n" + time_txt + " " + timeToStore + "\n" + done);
            } else {
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
