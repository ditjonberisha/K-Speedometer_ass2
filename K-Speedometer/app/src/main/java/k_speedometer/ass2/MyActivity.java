package k_speedometer.ass2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MyActivity extends Activity implements LocationListener,View.OnClickListener, SensorEventListener {

    Speedometer speedometer;
    private ImageButton imgLight;
    private ImageButton imgBalance;
    private ImageButton imgMap;
    private ImageButton StartCamera;
    boolean isFlashOn = false;//to check if light is on or off.
    public static Camera cam = null;
    private ImageView compass;
    private float currentDegree = 0f; // record the compass picture angle turned
    private SensorManager mSensorManager;
    private TextView txtGrade;
    private TextView tvLat;
    private TextView tvLong;
    private TextView tvCity;
    private LocationManager locationM;
    private Location location;
    private String provider;
    private String gps;
    private String LightOn;
    private String LightOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        compass = (ImageView) findViewById(R.id.imageViewCompass);
        txtGrade = (TextView) findViewById(R.id.textViewGrade);
        speedometer = (Speedometer) findViewById(R.id.Speedometer);
        imgLight = (ImageButton) findViewById(R.id.imgLight);
        imgMap = (ImageButton) findViewById(R.id.imgMap);
        imgBalance = (ImageButton) findViewById(R.id.imgBalance);
        StartCamera = (ImageButton) findViewById(R.id.imgCamera);
        tvLat = (TextView) findViewById(R.id.textViewlat);
        tvLong = (TextView) findViewById(R.id.textViewlong);
        tvCity = (TextView) findViewById(R.id.textViewCity);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        locationM = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria c=new Criteria();

        provider=locationM.getBestProvider(c, false);

        location=locationM.getLastKnownLocation(provider);
        if(location!=null)
        {
            //get latitude and longitude of the location
            double lng=location.getLongitude();
            double lat=location.getLatitude();
            //display on text view
            tvLong.setText(""+lng);
            tvLat.setText(""+lat);

        }
        else
        {
            tvLong.setText("n/a");
            tvLat.setText("n/a");

            gps = getResources().getString(R.string.no_gps);
            speedometer.onSpeedChanged(0);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(gps);
            alert.setPositiveButton("OK", null);
            alert.show();
        }

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.onLocationChanged(null);
        imgLight.setOnClickListener(this);
        imgBalance.setOnClickListener(this);
        imgMap.setOnClickListener(this);
        StartCamera.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location != null){

            double lng=location.getLongitude();
            double lat=location.getLatitude();
            tvLong.setText(""+lng);
            tvLat.setText(""+lat);

            try {
                // get name of the city
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                addresses = gcd.getFromLocation(lat, lng, 1);
                if (addresses.size() > 0) {
                    tvCity.setText(addresses.get(0).getLocality());
                } else
                    tvCity.setText("City not found");
            } catch (IOException e) {
                e.printStackTrace();
            }

            float currentspeed = location.getSpeed() * 36/10;
            speedometer.onSpeedChanged(currentspeed);
        }else{
            tvLong.setText("n/a");
            tvLat.setText("n/a");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

     //Camera Flash Light Code
    public void flashLightOn(View view) {

        LightOn = getResources().getString(R.string.exeptionOn);
        try {
            //check if there is a Camera Flash
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();//open camera access
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//get torch
                cam.setParameters(p);
                cam.startPreview();//start torch
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), LightOn,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void flashLightOff(View view) {
        LightOff = getResources().getString(R.string.exeptionOff);
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();//Stop camera flash light
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), LightOff,
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgLight:
            if (!isFlashOn)//check if flash is Off
            {
                flashLightOn(view);//turn it on
                isFlashOn = true;//set variable true for On
                imgLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_on));//change image background
            } else {
                flashLightOff(view);
                isFlashOn = false;
                imgLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_off));
            }break;
            //end Camera Flash Light
            case R.id.imgBalance:
                Intent i = new Intent("android.intent.action.BALANCE");
                startActivity(i);
                break;
            case R.id.imgCamera:
                //start camera to take a picture
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //start activity to get a result
                startActivity(i);
                break;
            case R.id.imgMap:
                Intent i1 = new Intent("android.intent.action.MAP");
                startActivity(i1);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        txtGrade.setText(Float.toString(degree) + (char) 0x00B0);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        compass.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
