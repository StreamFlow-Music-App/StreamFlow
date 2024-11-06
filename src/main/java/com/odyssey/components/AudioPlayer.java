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

    private OnSongEndListener onSongEndListener;

    public void setOnSongEndListener(OnSongEndListener listener) {
        this.onSongEndListener = listener;
    }

    public void play(String songPath) {
        try {
            stop();

            currentSongPath = songPath;
            fileInputStream = new FileInputStream(currentSongPath);
            player = new Player(fileInputStream);

            if (isPaused) {
                isPaused = false;
            }

            new Thread(() -> {
                try {
                    player.play();
                    if (player != null && player.isComplete() && onSongEndListener != null) {
                        onSongEndListener.onSongEnd();
                    }
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
            pausePosition = fileInputStream.available();
            player.close();
            player = null;
            isPaused = true;
        }
    }

    public void resume() {
        if (isPaused) {
            try {
                fileInputStream = new FileInputStream(currentSongPath);
                play(currentSongPath);
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
        }
    }

    public interface OnSongEndListener {
        void onSongEnd();
    }
}