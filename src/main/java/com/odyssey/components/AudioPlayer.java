package com.odyssey.components;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {
    private Player player;
    private String currentSongPath;
    private FileInputStream fileInputStream;
    private int pausedPosition = 0;  // Store the paused position in bytes
    private boolean isPaused = false;

    // Play or resume the song
    public void play(String songPath) {
        try {
            if (isPaused && currentSongPath.equals(songPath)) {
                // Resume from paused position
                resume();
            } else {
                stop();
                currentSongPath = songPath;
                fileInputStream = new FileInputStream(currentSongPath);
                player = new Player(fileInputStream);
                playFromBeginning();
            }
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    // Method to pause the song
    public void pause() {
        if (player != null) {
            try {
                pausedPosition = fileInputStream.available();  // Store remaining bytes to approximate the position
                player.close();
                isPaused = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to stop the player
    public void stop() {
        if (player != null) {
            player.close();
            player = null;
            isPaused = false;
            pausedPosition = 0;
        }
    }

    // Method to resume playback from paused position
    private void resume() {
        try {
            fileInputStream = new FileInputStream(currentSongPath);
            fileInputStream.skip(fileInputStream.available() - pausedPosition);  // Skip to the approximate position
            player = new Player(fileInputStream);
            playFromBeginning();
            isPaused = false;
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    // Helper to start playback in a new thread
    private void playFromBeginning() {
        new Thread(() -> {
            try {
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public boolean isPaused() {
        return isPaused;
    }
    public String getCurrentSongPath() {
        return currentSongPath;
    }
}
