package k_speedometer.ass2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/*
    Created by Arnold, Ditjon and Blend
 */

public class Balance extends Activity implements SensorEventListener {

    //Declaration of variables for the entire class
    public float centerX;
    public float centerY;
    public float sensorX;
    public float sensorY;
    public int nrPixels;
    public int range;
    private Bitmap Ball;
    private Bitmap gPlus;
    private MyBringBackSurface ourSurfaceHolder;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Canvas canvas;

    public class MyBringBackSurface extends SurfaceView implements Runnable {

        //Declaration of variables for the MyBringBackSurface object
        private SurfaceHolder ourHolder;
        private Thread ourThread = null;
        private boolean isRunning = true;

        public MyBringBackSurface(Context context) {
            super(context);

            ourHolder = getHolder();
            ourThread = new Thread(this);
            ourThread.start();
        }

        //If the activity pauses this method stops the thread
        public void pause() {
            isRunning = false;
            while (true) {
                try {
                    ourThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            ourThread = null;
        }

        //When the the activity comes back into the screen this method starts the thread again
        public void resume() {
            isRunning = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        //During the time that the thread is working this method draws the ball into the screen
        @Override
        public void run() {
            while (isRunning) {
                if (!ourHolder.getSurface().isValid())
                    continue;

                canvas = ourHolder.lockCanvas();

                //Gets the coordinates to where the ball will be drawn
                centerX = (canvas.getWidth() / 2) - (Ball.getWidth() / 2);
                centerY = (canvas.getHeight() / 2) - (Ball.getHeight() / 2);

                canvas.drawRGB(255, 255, 255); //Draws the background of the canvas (White in this case)

                //Draws the ball and the plus image into the center of the screen
                canvas.drawBitmap(Ball, centerX + sensorX * nrPixels, centerY + sensorY * nrPixels, null);
                canvas.drawBitmap(gPlus, (canvas.getWidth() / 2) - (gPlus.getWidth() / 2), (canvas.getHeight() / 2) - (gPlus.getHeight() / 2), null);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //The number of pixels the ball will move when the balance of the device changes
        nrPixels = 75;

        //Radius of the circle at the center of screen
        //If the center of the ball is inside this circle the device will be considered to be in balance
        range = 24;

        //Gets the accelerometer sensor
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (senSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            senAccelerometer = senSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //Gets the images and assigns them to the proper Bitmap variables
        Ball = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        gPlus = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        sensorX = sensorY = 0;

        //Every time this activity starts it will create a new MyBringBackSurface object
        ourSurfaceHolder = new MyBringBackSurface(this);
        ourSurfaceHolder.resume(); //Starts the thread of the MyBringBackSurface object
        setContentView(ourSurfaceHolder);
    }

    //Checks if the ball is at the center of the screen
    public boolean checkBalance() {
        if ((centerX + sensorX * nrPixels >= centerX && centerX + sensorX * nrPixels <= centerX + range
                || centerX + sensorX * nrPixels <= centerX && centerX + sensorX * nrPixels >= centerX - range)
                && (centerY + sensorY * nrPixels >= centerY && centerY + sensorY * nrPixels <= centerY + range
                || centerY + sensorY * nrPixels <= centerY && centerY + sensorY * nrPixels >= centerY - range))
            return true;
        else
            return false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Gets the values of the x and y-axis whenever the devices balance changes
        sensorX = sensorEvent.values[0];
        sensorY = sensorEvent.values[1];

        //If the the device is in balance the ball will be colored green else red
        if (checkBalance()) {
            Ball = BitmapFactory.decodeResource(getResources(), R.drawable.greenball);
        } else {
            Ball = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
        ourSurfaceHolder.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        ourSurfaceHolder.resume();
    }
}