package k_speedometer.ass2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class MyActivity extends Activity implements LocationListener,View.OnClickListener{

    Speedometer speedometer;
    ImageButton imgLight, imgCamera;
    boolean isFlashOn = false;//to check if light is on or off.
    public static Camera cam = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        speedometer = (Speedometer) findViewById(R.id.Speedometer);
        imgLight = (ImageButton) findViewById(R.id.imgLight);
        imgCamera = (ImageButton) findViewById(R.id.imgStartCamera);
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.onLocationChanged(null);
        imgLight.setOnClickListener(this);
        imgCamera.setOnClickListener(this);
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
        if(location == null){
            speedometer.onSpeedChanged(0);
        }
        else {
            float currentspeed = location.getSpeed() * 36/10;

            speedometer.onSpeedChanged(currentspeed);
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
            Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void flashLightOff(View view) {
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();//Stop camera flash light
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOff",
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
                imgLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_on));//change image button image background
            } else {
                flashLightOff(view);
                isFlashOn = false;
                imgLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_off));
            }break;
            //end Camera Flash Light
            case R.id.imgStartCamera:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//Get camera for capture
                startActivity(i);//start activity (camera)
        }
    }
}
