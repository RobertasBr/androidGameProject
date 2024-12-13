package com.example.finalprojectgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.List;

public class PlayScreenActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private List<Integer> sequence;
    private int sequenceIndex = 0;
    private int score = 0;
    private boolean gameStarted = false;

    private static final int COLOR_RED = 0;
    private static final int COLOR_BLUE = 1;
    private static final int COLOR_GREEN = 2;
    private static final int COLOR_YELLOW = 3;

    private static final float TILT_THRESHOLD_X = 5f;
    private static final float TILT_THRESHOLD_Y = 5f;

    private Button buttonRed, buttonBlue, buttonGreen, buttonYellow;
    private ToneGenerator toneGenerator;
    private ConstraintLayout playLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_screen);

        playLayout = findViewById(R.id.playLayout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        sequence = getIntent().getIntegerArrayListExtra("sequence");

        if (sequence == null) {
            sequence = new ArrayList<>();
            sequence.add(COLOR_RED);
            sequence.add(COLOR_BLUE);
            sequence.add(COLOR_GREEN);
            sequence.add(COLOR_YELLOW);
        }

        buttonRed = findViewById(R.id.buttonRed);
        buttonBlue = findViewById(R.id.buttonBlue);
        buttonGreen = findViewById(R.id.buttonGreen);
        buttonYellow = findViewById(R.id.buttonYellow);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (!gameStarted) {
                gameStarted = true;
                Toast.makeText(this, "Game Started! Match the sequence.", Toast.LENGTH_SHORT).show();
            }

            float x = event.values[0];
            float y = event.values[1];

            if (y > TILT_THRESHOLD_Y) {
                Log.d("PlayScreenActivity", "Tilted Up");
                checkSequence(COLOR_YELLOW);
                changeBackgroundColor(Color.YELLOW);
            } else if (y < -TILT_THRESHOLD_Y) {
                Log.d("PlayScreenActivity", "Tilted Down");
                checkSequence(COLOR_RED);  // Red
                changeBackgroundColor(Color.RED);
            } else if (x > TILT_THRESHOLD_X) {
                Log.d("PlayScreenActivity", "Tilted Left");
                checkSequence(COLOR_BLUE);
                changeBackgroundColor(Color.BLUE);
            } else if (x < -TILT_THRESHOLD_X) {
                Log.d("PlayScreenActivity", "Tilted Right");
                checkSequence(COLOR_GREEN);
                changeBackgroundColor(Color.GREEN);
            }
        }
    }

    private void checkSequence(int color) {
        if (sequenceIndex < sequence.size()) {
            if (sequence.get(sequenceIndex) == color) {
                sequenceIndex++;
                if (sequenceIndex == sequence.size()) {
                    score += 4;
                    sequenceIndex = 0;
                    Toast.makeText(this, "Correct! Next sequence.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlayScreenActivity.this, SequenceLearningActivity.class);
                    sequence.add((int) (Math.random() * 4));
                    intent.putIntegerArrayListExtra("sequence", (ArrayList<Integer>) sequence);
                    intent.putExtra("score", score);
                    startActivity(intent);
                    finish();
                }
            } else {
                //gameover
            }
        }
    }


    private void changeBackgroundColor(int color) {
        playLayout.setBackgroundColor(color);
        Log.d("PlayScreenActivity", "Background color changed to: " + color);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("PlayScreenActivity", "Accelerometer working");
        } else {
            Log.d("PlayScreenActivity", "Accelerometer not working");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d("PlayScreenActivity", "Accelerometer unregistered");
        }
        if (toneGenerator != null) {
            toneGenerator.release();
        }
    }
}
