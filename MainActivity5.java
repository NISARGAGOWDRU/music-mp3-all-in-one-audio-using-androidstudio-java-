package com.example.dreamland;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

public class MainActivity5 extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";
    private String mode = "OFF";
    private ImageView pausePlayBtn, pausePlayBtn1, nextBtn, previousBtn;
    private TextView songNameTxt;
    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;

    private Button voiceEnableBtn;

    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> songs;
    SeekBar seekBar;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private boolean isPlaying = false; // Variable to track play/pause state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        initializeViews();
        initializeSpeechRecognizer();
        checkVoiceCommandPermission();
        validateAndStartPlaying();
        setupListeners();

        // Add SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    mediaPlayer.start(); // Add this line to start playing from the new position
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause(); // Pause the media player while the user is adjusting the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start(); // Optional: Resume playing after the user has selected a new position
            }
        });

        // Update SeekBar progress as the media is being played
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        // Update SeekBar position
                        final int currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);

                        // Sleep for 1 second
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopMediaPlayer(); // Stop or release MediaPlayer when back button is pressed
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null; // Set to null to indicate it's no longer in use
        }
    }

    private void initializeViews() {
        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        pausePlayBtn = findViewById(R.id.play_pause_btn);
        pausePlayBtn1 = findViewById(R.id.play_pause_btn1);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        imageView = findViewById(R.id.logo);
        seekBar = findViewById(R.id.seekBar);
        songNameTxt = findViewById(R.id.songName);
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity5.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null) {
                    keeper = matchesFound.get(0);
                    Toast.makeText(MainActivity5.this, "Result = " + keeper, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void validateAndStartPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            songs =(ArrayList) bundle.getParcelableArrayList("song");
            if (songs != null && !songs.isEmpty()) {
                position = bundle.getInt("position", 0);
                String songName = songs.get(position).getName();
                songNameTxt.setText(songName);
                songNameTxt.setSelected(true);
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
            }
        }
    }

    private void setupListeners() {
        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return true;
            }
        });

        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause(); // Toggle play/pause when button is clicked
            }
        });

        pausePlayBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause(); // Toggle play/pause when button is clicked
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < songs.size() - 1) {
                    position++;
                    playSong();
                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    position--;
                    playSong();
                }
            }
        });
    }

    private void togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.pause();
            pausePlayBtn.setImageResource(R.drawable.play); // Change the icon to play
            pausePlayBtn1.setImageResource(R.drawable.play); // Change the icon to play
        } else {
            mediaPlayer.start();
            pausePlayBtn.setImageResource(R.drawable.pause); // Change the icon to pause
            pausePlayBtn1.setImageResource(R.drawable.pause); // Change the icon to pause
        }
        // Toggle play/pause state
        isPlaying = !isPlaying;
    }

    private void playSong() {
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            String songName = songs.get(position).getName();
            songNameTxt.setText(songName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkVoiceCommandPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int recordAudioPermission = ContextCompat.checkSelfPermission(MainActivity5.this, Manifest.permission.RECORD_AUDIO);
            int microphonePermission = ContextCompat.checkSelfPermission(MainActivity5.this, Manifest.permission.RECORD_AUDIO);
            if (recordAudioPermission != PackageManager.PERMISSION_GRANTED || microphonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity5.this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.RECORD_AUDIO},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {

            }
        }
    }
}
