package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FocusActivity extends AppCompatActivity {

    private ProgressBar progressBarCircle;
    private TextView taskTime, focusTask,focusTaskBar;
    private EditText taskEditText;
    private CountDownTimer countDownTimer;
    private Button buttonStartPause, buttonReset;
    private ImageButton buttonBack;
    private boolean isTimerRunning = false;
    private boolean isBreakTime = false;
    private long timeInMillis = 25 * 60 * 1000;
    private long breakTimeInMillis = 5 * 60 * 1000;
    private long remainingTimeInMillis = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        progressBarCircle = findViewById(R.id.progressBarCircle);
        taskEditText = findViewById(R.id.taskEditText);
        focusTask = findViewById(R.id.focusTask);
        focusTaskBar = findViewById(R.id.focusTaskbar);
        taskTime = findViewById(R.id.focusEditTime);
        buttonStartPause = findViewById(R.id.buttonStartPause);
        buttonReset = findViewById(R.id.buttonReset);
        buttonBack = findViewById(R.id.focusback_btn);

        progressBarCircle.setMax((int) timeInMillis / 1000);

        updateTimer();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FocusActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        buttonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void startTimer() {
        if (remainingTimeInMillis > 0) {
            timeInMillis = remainingTimeInMillis;
            remainingTimeInMillis = 0;
        } else if (isBreakTime) {
            timeInMillis = breakTimeInMillis;
        } else {
            timeInMillis = 25 * 60 * 1000;
        }

        progressBarCircle.setMax((int) timeInMillis / 1000);
        progressBarCircle.setProgress((int) (timeInMillis / 1000));

        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeInMillis = millisUntilFinished;
                updateTimer();
                updateProgressBar();
            }
            @Override
            public void onFinish() {
                isTimerRunning = false;
                updateButtons();
            }
        }.start();

        isTimerRunning = true;
        updateButtons();

        String taskName = taskEditText.getText().toString();
        if (!taskName.isEmpty()) {
            focusTaskBar.setText("Focusing");
        }
    }

    private void startBreakTimer() {
        countDownTimer = new CountDownTimer(breakTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeInMillis = millisUntilFinished;
                updateTimer();
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                isBreakTime = false;
                updateButtons();
            }
        }.start();

        updateButtons();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
            remainingTimeInMillis = timeInMillis;
            updateButtons();
        }
    }

    private void resetTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        timeInMillis = 25 * 60 * 1000;
        remainingTimeInMillis = 0;
        updateTimer();
        updateProgressBar();
        updateButtons();
    }

    private void updateTimer() {
        int minutes = (int) (timeInMillis / 1000) / 60;
        int seconds = (int) (timeInMillis / 1000) % 60;
        String timeLeft = String.format("%02d:%02d", minutes, seconds);
        taskTime.setText(timeLeft);

        String taskName = taskEditText.getText().toString();
        if (!taskName.isEmpty()) {
            focusTask.setText(taskName);
            taskEditText.setVisibility(View.GONE);
        }
    }

    private void updateProgressBar() {
        progressBarCircle.setMax((int) timeInMillis / 1000);
        progressBarCircle.setProgress((int) timeInMillis / 1000);
    }

    private void updateButtons() {
        if (isTimerRunning) {
            buttonStartPause.setText("Pause");
        } else {
            buttonStartPause.setText("Start");
        }
    }
}