package com.example.finalprojectgame;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SequenceLearningActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private Button playButton;
    private List<Integer> sequence;
    private int sequenceIndex = 0;
    private static final int SEQUENCE_START_LENGTH = 4;
    private static final int SEQUENCE_INCREMENT = 2;
    private static final int COLOR_COUNT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sequence_learning);

        rootLayout = findViewById(R.id.constraintLayout);
        playButton = findViewById(R.id.playBtn);

        Intent intent = getIntent();
        sequence = intent.getIntegerArrayListExtra("sequence");
        int score = intent.getIntExtra("score", 0);

        if (sequence == null) {
            sequence = new ArrayList<>();
            generateSequence(SEQUENCE_START_LENGTH);
        }

        showSequence();

        playButton.setOnClickListener(v -> {
            Intent playIntent = new Intent(SequenceLearningActivity.this, PlayScreenActivity.class);
            playIntent.putIntegerArrayListExtra("sequence", (ArrayList<Integer>) sequence);
            startActivity(playIntent);
        });
    }


    private void generateSequence(int length) {
        Random random = new Random();
        sequence.clear();
        for (int i = 0; i < length; i++) {
            sequence.add(random.nextInt(COLOR_COUNT));
        }
    }

    private void showSequence() {
        sequenceIndex = 0;

        int[] circlePositions = {
                R.id.leftAnchor,
                R.id.topAnchor,
                R.id.rightAnchor,
                R.id.bottomAnchor
        };

        Handler handler = new Handler();
        for (int i = 0; i < sequence.size(); i++) {
            int colorIndex = sequence.get(i);
            int positionIndex = i % circlePositions.length;

            View colorCircle = createColorCircle(colorIndex);

            int anchorId = circlePositions[positionIndex];
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(200, 200);

            params.circleConstraint = anchorId;
            params.circleRadius = 0;
            params.circleAngle = 0;

            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

            colorCircle.setLayoutParams(params);
            rootLayout.addView(colorCircle);

            colorCircle.setVisibility(View.INVISIBLE);

            handler.postDelayed(() -> {
                colorCircle.setVisibility(View.VISIBLE);

                if (sequenceIndex == sequence.size() - 1) {
                    playButton.setVisibility(View.VISIBLE);
                    generateSequence(sequence.size() + SEQUENCE_INCREMENT);

                    Intent intent = new Intent(SequenceLearningActivity.this, PlayScreenActivity.class);
                    startActivity(intent);
                }
                sequenceIndex++;
            }, i * 1000);
        }
    }


    private View createColorCircle(int colorIndex) {
        View circle = new View(this);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(200, 200);

        circle.setLayoutParams(params);

        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(getCircleColor(colorIndex));

        circle.setBackground(circleDrawable);

        return circle;
    }

    private int getCircleColor(int colorIndex) {
        switch (colorIndex) {
            case 0:
                return getResources().getColor(android.R.color.holo_red_light, null);
            case 1:
                return getResources().getColor(android.R.color.holo_blue_light, null);
            case 2:
                return getResources().getColor(android.R.color.holo_orange_light, null);
            case 3:
                return getResources().getColor(android.R.color.holo_green_dark, null);
            default:
                return getResources().getColor(android.R.color.darker_gray, null);
        }
    }
}
