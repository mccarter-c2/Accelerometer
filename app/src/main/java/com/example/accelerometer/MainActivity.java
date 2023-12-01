package com.example.accelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean colour = false;
    private TextView view;
    private long lastUpdateTime;
    private static float SHAKE_THRESHOLD_GRAVITY = 2;
    private long lastUpdateProximity;
    TextView prox_output;
    Sensor proxSensor;
    Boolean isProxAvailable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // fall
        view = findViewById(R.id.txt_colour);
        view.setBackgroundColor(Color.GREEN);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdateTime = System.currentTimeMillis();

        // proximity
        prox_output = findViewById(R.id.prox_txt);
        sensorManager = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            isProxAvailable = true;
        } else {
            prox_output.setText("Proximity is not available!");
            isProxAvailable = false;
        }
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            getAccelerometer(sensorEvent);
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            getProximity(sensorEvent);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        long currentTime = System.currentTimeMillis();
        if (gForce >= SHAKE_THRESHOLD_GRAVITY)
        {
            if (currentTime - lastUpdateTime < 200){
                return;
            }
            lastUpdateTime = currentTime;

            view.setText("You shook me!");
            Toast.makeText(this,"Device was shaken", Toast.LENGTH_SHORT).show();
            if (colour == true){
                view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
            colour = !colour;
        }

    }

    private void getProximity(SensorEvent sensorEvent) {
        prox_output.setText("Proximity: " + sensorEvent.values[0] + " cm");
        float currentProx = sensorEvent.values[0];
        if (currentProx <= 2){
            Toast.makeText(this,"Device is too close!", Toast.LENGTH_SHORT).show();
            return;
        }
        lastUpdateProximity = (long) currentProx;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        //accelerometer sensors
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        // Proximity sensors
        if (isProxAvailable) {
            sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // UNREGISTER LISTENER
        sensorManager.unregisterListener(this);

        if (isProxAvailable) {
            sensorManager.unregisterListener(this);
        }
    }
}