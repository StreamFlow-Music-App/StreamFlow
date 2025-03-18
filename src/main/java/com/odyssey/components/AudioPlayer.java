package com.odyssey.components;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {
    private SpeedControlledPlayer player;
    private FileInputStream fileInputStream;
    private String currentSongPath;
    private long pausePosition = 0;
    private boolean isPaused = false;
    private long startTime = 0;
    private float playbackSpeed = 1.0f;

    private OnSongEndListener onSongEndListener;

    public void setOnSongEndListener(OnSongEndListener listener) {
        this.onSongEndListener = listener;
    }

    public void play(String songPath) {
        try {
            stop();

            currentSongPath = songPath;
            fileInputStream = new FileInputStream(currentSongPath);
            player = new SpeedControlledPlayer(fileInputStream);
            player.setPlaybackSpeed(playbackSpeed); // Set playback speed before playing

            if (isPaused) {
                isPaused = false;
            }

            startTime = System.currentTimeMillis() - pausePosition; // Adjust start time for resume
            new Thread(() -> {
                try {
                    player.setPlayBackListener(new PlaybackListener() {
                        @Override
                        public void playbackFinished(PlaybackEvent evt) {
                            if (onSongEndListener != null) {
                                try {
                                    onSongEndListener.onSongEnd();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() throws IOException {
        if (player != null) {
            pausePosition = getCurrentPlaybackTime();
            player.close();
            player = null;
            isPaused = true;
        }
    }

    public void resume() {
        if (isPaused) {
            try {
                fileInputStream = new FileInputStream(currentSongPath);
                player = new SpeedControlledPlayer(fileInputStream);
                player.setPlaybackSpeed(playbackSpeed); // Restore playback speed
                play(currentSongPath);
            } catch (IOException | JavaLayerException e) {
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
            startTime = 0;
        }
    }

    public long getCurrentPlaybackTime() {
        if (player != null && startTime > 0) {
            return (System.currentTimeMillis() - startTime) / 1000; // Return time in seconds
        }
        return pausePosition / 1000;
    }

    public void setPlaybackSpeed(float speed) {
        if (speed > 0) {
            this.playbackSpeed = speed;
            if (player != null) {
                stop();
                play(currentSongPath);
            }
            System.out.println("Playback speed set to: " + speed + "x");
        } else {
            System.out.println("Invalid playback speed. Speed must be greater than 0.");
        }
    }

    public interface OnSongEndListener {
        void onSongEnd() throws IOException;
    }
}


