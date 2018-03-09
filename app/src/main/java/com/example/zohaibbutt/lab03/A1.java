package com.example.zohaibbutt.lab03;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class A1 extends AppCompatActivity implements SensorEventListener {
    private boolean firstTimeRun = false;
    //sensor
    private Sensor accelerometer;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    // soundtrack
    private Uri notification;
    private Ringtone r;
    // circle
    private int marginRectangle = 30;
    private Ball ball = null;
    private int width;
    private int height;
    private int radius;
    private float circleX;
    private float circleY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set orientation to landscape
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        firstTimeRun = true;
        // set the default notification track
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        // set up the sensor
        setupSensor();
        // ball object
        this.ball = new Ball(this);
        setContentView(this.ball);
    }

    // unregister sensor listener onPause()
    @Override
    protected void onPause() {
        super.onPause();
        firstTimeRun = false;
        sensorManager.unregisterListener(this);
    }

    // Register sensor listener onResume()
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    // Set up the sensors
    private void setupSensor() {
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);

        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    // onSensorChanged checks of collision, set the circle position
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // new position for circle
        circleX = circleX + sensorEvent.values[1] * 2.0f;
        circleY = circleY + sensorEvent.values[0] * 2.0f;

        // collision check
        if(circleX <= marginRectangle + radius + 1) {
            circleX = marginRectangle + radius + 1;
            vibrator.vibrate(50);   // vibrate at collision
            r.play();                         // play the default notification sound at collision

        }
        if (circleX >= (width - marginRectangle) - radius + 1) {
            circleX = (width-marginRectangle) - radius + 1;
            vibrator.vibrate(50);
            r.play();
        }
        if (circleY <= marginRectangle + radius + 1) {
            circleY = marginRectangle + radius + 1;
            vibrator.vibrate(50);
            r.play();
        }
        if (circleY >= (height-marginRectangle) - radius + 1) {
            circleY = (height-marginRectangle) - radius + 1;
            vibrator.vibrate(50);
            r.play();
        }
        Log.i("Circle_X_And_Y", "circleX: " + circleX + ", " + "circleY: " + circleY);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public class Ball extends View {
        Paint paint = null;
        // Rectangle
        private ShapeDrawable rect;

        public Ball(Context context) {
            super(context);
            paint = new Paint();
        }
        // setupShapes sets the initial position and colours
        void setupShapes(){
            screenSizeWithoutNotification();

            // Rectangle
            this.rect = new ShapeDrawable(new RectShape());
            this.rect.getPaint().setColor(Color.BLACK);
            this.rect.setBounds(marginRectangle, marginRectangle, width - marginRectangle, height - marginRectangle);

            // Circle
            radius = (5 * width) / 100;
            circleX = width / 2;
            circleY = height / 2;
        }

        void screenSizeWithoutNotification(){
            View size = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            height = size.getHeight();
            width = size.getWidth();
            Log.i("WIDTH_AND_HEIGHT", "w:" + width + ", h:" + height);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // check if the its the first time and setup the shapes
            if(firstTimeRun){
                setupShapes();
                firstTimeRun = false;
            }

            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            // checks if screen is not locked
            if(!keyguardManager.inKeyguardRestrictedInputMode()) {
                // draw Rectangle
                rect.draw(canvas);
                // draw circle
                paint.setColor(Color.GREEN);
                canvas.drawCircle(circleX, circleY, radius, paint);

                invalidate();
            } else {
                firstTimeRun = false;
            }
        }
    }
}
