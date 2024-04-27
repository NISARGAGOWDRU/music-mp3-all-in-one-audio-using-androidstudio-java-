package com.example.dreamland;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.File;
import java.util.ArrayList;

public class MainActivity4 extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;

    private String[] itemsAll;
    private FloatingActionButton fab;
    private ListView mSongList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        // Initialize ListView and SearchView
        mSongList = findViewById(R.id.songsList);
        searchView = findViewById(R.id.searchView);


        // Request storage permission
        requestStoragePermission();

        // Set up the SearchView
        setupSearchView();

    }



    private void requestStoragePermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        // Permission is granted, you can proceed with your action
                        displayAudioSongsName();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        // Permission is denied, inform the user or handle it accordingly
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        // This method will be called if the user denies the permission
                        // You can show a rationale here and then request the permission again
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private ArrayList<File> retrieveAudioFiles(File directory) {
        ArrayList<File> audioFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    audioFiles.addAll(retrieveAudioFiles(file));
                } else {
                    String name = file.getName();
                    // Check if the file is an audio file
                    if (name.endsWith(".mp3") || name.endsWith(".aac") || name.endsWith(".wav") || name.endsWith(".wma") || name.endsWith(".m4a") ||
                            name.endsWith(".flac") || name.endsWith(".ogg") || name.endsWith(".aiff") || name.endsWith(".alac") || name.endsWith(".ape") ||
                            name.endsWith(".ac3") || name.endsWith(".amr") || name.endsWith(".dsd") || name.endsWith(".midi") || name.endsWith(".mid")) {
                        audioFiles.add(file);
                    }
                }
            }
        }
        return audioFiles;
    }

    private void displayAudioSongsName() {
        final ArrayList<File> audioSongs = retrieveAudioFiles(Environment.getExternalStorageDirectory());
        itemsAll = new String[audioSongs.size()];
        for (int i = 0; i < audioSongs.size(); i++) {
            itemsAll[i] = audioSongs.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsAll);
        mSongList.setAdapter(arrayAdapter);
        mSongList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String songName = audioSongs.get(i).getName();
                Intent intent = new Intent(MainActivity4.this, MainActivity5.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // if you want to handle the search query when the user submits
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list as per the search text
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        ArrayList<File> filteredList = new ArrayList<>();
        for (File file : retrieveAudioFiles(Environment.getExternalStorageDirectory())) {
            if (file.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(file);
            }
        }
        // Update the ListView with the filtered list
        updateListView(filteredList);
    }

    private void updateListView(ArrayList<File> filteredList) {
        itemsAll = new String[filteredList.size()];
        for (int i = 0; i < filteredList.size(); i++) {
            itemsAll[i] = filteredList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsAll);
        mSongList.setAdapter(arrayAdapter);
    }
}
