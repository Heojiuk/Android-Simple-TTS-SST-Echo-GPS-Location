package kr.ac.mokwon.ice.testandroid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Toast;

public class MySensorListener implements SensorEventListener {
    public double accelX, accelY, accelZ;
    Context context;

    public MySensorListener(Context context) {
        this.context = context;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = sensorEvent.values[0];
            accelY = sensorEvent.values[1];
            accelZ = sensorEvent.values[2];
            //  Toast.makeText(context,"x => "+accelX+" y => "+accelY+" z => "+accelZ,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
