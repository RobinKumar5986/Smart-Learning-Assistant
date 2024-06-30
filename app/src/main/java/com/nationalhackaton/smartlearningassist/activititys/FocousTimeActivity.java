package com.nationalhackaton.smartlearningassist.activititys;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nationalhackaton.smartlearningassist.R;
import com.nationalhackaton.smartlearningassist.databinding.ActivityFocousTimeBinding;

public class FocousTimeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityFocousTimeBinding binding;
    int timer = 0;
    int secs = 0;
    Handler handler = new Handler();
    Runnable timerRunnable;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFocousTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        InitializeView();
    }

    private void InitializeView() {
        binding.btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btnStart.getId()) {
            StartTimer();
        }
    }

    private void StartTimer() {
        String time = binding.txtTime.getEditText().getText().toString().trim();
        if (!time.isEmpty()) {
            // Convert time to seconds
            secs = (int) (Double.parseDouble(time) * 3600);

            // Reset timer and progress bar
            timer = 0;
            binding.circularProgressBar.setProgress(0);

            // Stop the alarm sound if it's playing
            stopAlarmSound();

            // Remove any existing callbacks to prevent interference
            handler.removeCallbacksAndMessages(null);

            // Start the timer runnable
            StartTimerRunnable();
        } else {
            Toast.makeText(this, "Enter the time to focus...", Toast.LENGTH_SHORT).show();
        }
    }

    private void StartTimerRunnable() {
        // Create a new runnable to update timer and progress bar
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timer++;
                // Update progress bar if needed
                updateProgressBar();

                if (timer < secs) {
                    handler.postDelayed(this, 1000); // Repeat every second (1000ms)
                } else {
                    // Timer has reached the specified duration (secs)
                    // Perform any action here when timer completes
                    timerCompleted();
                }
            }
        };

        // Start the runnable for the first time
        handler.post(timerRunnable);
    }

    private void updateProgressBar() {
        // Calculate progress percentage
        int progress = (int) (((float) timer / secs) * 100);
        binding.circularProgressBar.setProgress(progress);

        // Update TextViews for HH:MM:SS if needed
        updateTimerTextViews();
    }

    private void updateTimerTextViews() {
        // Calculate hours, minutes, and seconds remaining
        int remainingHours = (secs - timer) / 3600;
        int remainingMinutes = ((secs - timer) % 3600) / 60;
        int remainingSeconds = (secs - timer) % 60;

        // Update your TextViews for HH:MM:SS
        binding.tvHours.setText(String.format("%02d", remainingHours));
        binding.tvMinutes.setText(String.format("%02d", remainingMinutes));
        binding.tvSeconds.setText(String.format("%02d", remainingSeconds));
    }

    private void timerCompleted() {
        // Stop the handler
        handler.removeCallbacks(timerRunnable);

        // Play alarm sound
        playAlarmSound();

        // Optionally, show a toast or perform other actions
        Toast.makeText(this, "Timer completed!", Toast.LENGTH_SHORT).show();
    }

    private void playAlarmSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.setLooping(true); // Set looping to true if you want it to repeat
        mediaPlayer.start();
    }

    private void stopAlarmSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Remove callbacks to prevent memory leaks
        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }
}
