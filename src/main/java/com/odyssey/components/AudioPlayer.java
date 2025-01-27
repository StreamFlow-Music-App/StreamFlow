
package com.odyssey.components;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {
    private Player player;
    private FileInputStream fileInputStream;
    private String currentSongPath;
    private long pausePosition = 0;
    private boolean isPaused = false;
    private float playbackSpeed = 1.0f;
    private OnSongEndListener onSongEndListener;
    private long elapsedTime = 0; // Elapsed time in milliseconds
    private boolean isPlaying = false; // To track if the song is currently playing

    private String formatTime(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setPlaybackSpeed(float speed) {
        if (speed >= 0.5f && speed <= 2.0f) { // Limit speed between 0.5x and 2.0x
            playbackSpeed = speed;
            System.out.println("Playback speed set to " + playbackSpeed + "x");
        } else {
            System.out.println("Invalid speed. Please use a value between 0.5x and 2.0x.");
        }
    }



    public void setOnSongEndListener(OnSongEndListener listener) {
        this.onSongEndListener = listener;
    }

    public void play(String songPath) {
        try {
            stop();

            currentSongPath = songPath;
            fileInputStream = new FileInputStream(currentSongPath);
            player = new Player(fileInputStream);


            elapsedTime = 0;
            isPlaying = true;


            if (isPaused) {
                isPaused = false;
            }

            new Thread(() -> {
                try {
                    // Timer thread for updating elapsed time
                    new Thread(() -> {
                        while (isPlaying) {
                            try {
                                Thread.sleep(1000); // Update every second
                                elapsedTime += 1000;
                                System.out.print("\rElapsed Time: " + formatTime(elapsedTime)); // Display timer
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    // Playback thread with speed control
                    while (!player.isComplete()) {
                        if (player == null) {
                            System.err.println("Error: Player is null. Stopping playback.");
                            isPlaying = false;
                            break;
                        }

                        if (playbackSpeed != 1.0f) {
                            Thread.sleep((long) (100 / playbackSpeed)); // Adjust playback speed
                            player.play(1); // Play one frame
                        } else {
                            player.play(1); // Normal playback
                        }
                    }

                    // If playback completes, reset state
                    if (player.isComplete()) {
                        isPlaying = false;
                        elapsedTime = 0;
                        System.out.println("\nPlayback finished!");
                        if (onSongEndListener != null) {
                            onSongEndListener.onSongEnd();
                        }
                    }
                } catch (JavaLayerException | InterruptedException e) {
                    System.err.println("Playback error: " + e.getMessage());
                }
            }).start();

        } catch (JavaLayerException | IOException e) {
            System.err.println("Error initializing playback: " + e.getMessage());
        }
    }


    public void pause() throws IOException {
        if (player != null) {
            pausePosition = fileInputStream.available();
            player.close();
            player = null;
            isPlaying = false;
            System.out.println("\nPlayback paused at: " + formatTime(elapsedTime));
        }
    }

    public void resume() {
        if (isPaused) {
            try {
                fileInputStream = new FileInputStream(currentSongPath);
                play(currentSongPath);
                isPlaying = true;
                System.out.println("Resuming playback...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
            isPaused = false;
            pausePosition = 0;
            elapsedTime = 0;
            System.out.println("\nPlayback stopped.");
        }
    }

    public interface OnSongEndListener {
        void onSongEnd();
    }
}
