package com.example.mini_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private Tamagotchi tamagotchi;
    private Handler handler;
    private Runnable updateRunnable;

    private long updateInterval = 2000L;
    private final long speedIncreaseTime = 30000L;
    private long lastSpeedIncrease = 0L;

    private ImageView tamagotchiImage;
    private TextView statusText, hungerText, tirednessText, boredomText, happinessText, timeText, bestTimeText;
    private MaterialButton feedButton, sleepButton, playButton, restartButton;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "TamagotchiPrefs";
    private static final String BEST_TIME_KEY = "best_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tamagotchi = new Tamagotchi();
        handler = new Handler(Looper.getMainLooper());
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        tamagotchi.setBestTime(prefs.getLong(BEST_TIME_KEY, 0));

        initializeViews();
        setupButtons();
        startGameLoop();
        updateUI();
    }

    private void initializeViews() {
        tamagotchiImage = findViewById(R.id.tamagotchi_image);
        statusText = findViewById(R.id.status_text);
        hungerText = findViewById(R.id.hunger_text);
        tirednessText = findViewById(R.id.tiredness_text);
        boredomText = findViewById(R.id.boredom_text);
        happinessText = findViewById(R.id.happiness_text);
        timeText = findViewById(R.id.time_text);
        bestTimeText = findViewById(R.id.best_time_text);

        feedButton = findViewById(R.id.feed_button);
        sleepButton = findViewById(R.id.sleep_button);
        playButton = findViewById(R.id.play_button);
        restartButton = findViewById(R.id.restart_button);

        restartButton.setVisibility(View.GONE);
    }

    private void setupButtons() {
        feedButton.setOnClickListener(v -> {
            if (tamagotchi.isAlive()) {
                tamagotchi.feed();
                updateUI();
            }
        });

        sleepButton.setOnClickListener(v -> {
            if (tamagotchi.isAlive()) {
                tamagotchi.sleep();
                updateUI();
            }
        });

        playButton.setOnClickListener(v -> {
            if (tamagotchi.isAlive()) {
                tamagotchi.play();
                updateUI();
            }
        });

        restartButton.setOnClickListener(v -> restartGame());
    }

    private void startGameLoop() {
        lastSpeedIncrease = System.currentTimeMillis();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (!tamagotchi.isAlive()) {
                    gameOver();
                    return;
                }

                tamagotchi.update();
                updateUI();

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSpeedIncrease >= speedIncreaseTime) {
                    updateInterval = Math.max(500L, updateInterval - 200L);
                    lastSpeedIncrease = currentTime;
                }

                handler.postDelayed(this, updateInterval);
            }
        };

        handler.post(updateRunnable);
    }

    private void restartGame() {
        handler.removeCallbacks(updateRunnable);

        tamagotchi.reset();
        updateInterval = 2000L;
        lastSpeedIncrease = System.currentTimeMillis();
        restartButton.setVisibility(View.GONE);

        updateUI();
        handler.post(updateRunnable);
    }

    private void updateUI() {
        hungerText.setText("Голод: " + tamagotchi.getHunger());
        tirednessText.setText("Усталость: " + tamagotchi.getTiredness());
        boredomText.setText("Скука: " + tamagotchi.getBoredom());
        happinessText.setText("Счастье: " + tamagotchi.getHappiness());
        timeText.setText("Время жизни: " + tamagotchi.getTimeAlive() + " сек");
        bestTimeText.setText("Лучшее время: " + tamagotchi.getBestTime() + " сек");

        if (tamagotchi.isAlive()) {
            statusText.setText("Состояние: Жив");

            if (tamagotchi.getHappiness() > 70 && tamagotchi.getHunger() < 40 && tamagotchi.getTiredness() < 40) {
                tamagotchiImage.setImageResource(R.drawable.happy);
            } else if (tamagotchi.getHappiness() > 30) {
                tamagotchiImage.setImageResource(R.drawable.neutral);
            } else {
                tamagotchiImage.setImageResource(R.drawable.sad);
            }
        } else {
            statusText.setText("Состояние: Мёртв");
            tamagotchiImage.setImageResource(R.drawable.dead);
        }
    }

    private void gameOver() {
        long timeAlive = tamagotchi.getTimeAlive();
        long bestTime = tamagotchi.getBestTime();

        if (timeAlive > bestTime) {
            tamagotchi.setBestTime(timeAlive);
            prefs.edit().putLong(BEST_TIME_KEY, timeAlive).apply();
        }

        updateUI();
        restartButton.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Игра окончена. Ты прожил " + timeAlive + " сек.", Toast.LENGTH_LONG).show();
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}