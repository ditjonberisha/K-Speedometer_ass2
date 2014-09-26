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


public class Balance extends Activity implements SensorEventListener {

    float sensorX, sensorY;
    Bitmap Ball, gPlus;
    MyBringBackSurface ourSurfaceHolder;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    Canvas canvas;
    float centerX, centerY;
    int nrPixels, range;

    public class MyBringBackSurface extends SurfaceView implements Runnable {

        SurfaceHolder ourHolder;
        Thread ourThread = null;
        boolean isRunning = true;

        public MyBringBackSurface(Context context) {
            super(context);
            ourHolder = getHolder();

            ourThread = new Thread(this);
            ourThread.start();
        }

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

        public void resume() {
            isRunning = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        @Override
        public void run() {
            while (isRunning) {
                if (!ourHolder.getSurface().isValid())
                    continue;

                canvas = ourHolder.lockCanvas();

                centerX = (canvas.getWidth() / 2) - (Ball.getWidth() / 2);
                centerY = (canvas.getHeight() / 2) - (Ball.getHeight() / 2);

                canvas.drawRGB(255, 255, 255);

                canvas.drawBitmap(Ball, centerX + sensorX * nrPixels, centerY + sensorY * nrPixels, null);
                canvas.drawBitmap(gPlus, (canvas.getWidth() / 2) - (gPlus.getWidth() / 4), (canvas.getHeight() / 2) - (gPlus.getHeight() / 2), null);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nrPixels = 75;

        range = 24;

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (senSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            senAccelerometer = senSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Ball = BitmapFactory.decodeResource(getResources(), R.drawable.blackball);
        gPlus = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        sensorX = sensorY = 0;

        ourSurfaceHolder = new MyBringBackSurface(this);
        ourSurfaceHolder.resume();
        setContentView(ourSurfaceHolder);
    }

    private boolean checkBalance() {
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
        try {
            Thread.sleep(75);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sensorX = sensorEvent.values[0];
        sensorY = sensorEvent.values[1];

        if (checkBalance()) {
            Ball = BitmapFactory.decodeResource(getResources(), R.drawable.blueball);
        } else {
            Ball = BitmapFactory.decodeResource(getResources(), R.drawable.blackball);
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