package com.odyssey.controllers;

import com.odyssey.components.AudioPlayer;

import java.io.IOException;

public class PlayerController {
    private final AudioPlayer audioPlayer;
    private final OnSongEndCallback onSongEndCallback;

    public PlayerController(OnSongEndCallback callBack) {
        this.audioPlayer = new AudioPlayer();
        this.onSongEndCallback = callBack;

        audioPlayer.setOnSongEndListener(() -> {
            try {
                if (onSongEndCallback != null) {
                    onSongEndCallback.onSongEnd();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void play(String songPath) {
        audioPlayer.play(songPath);
    }

    public void pause() throws IOException {
        audioPlayer.pause();
    }

    public void resume() {
        audioPlayer.resume();
    }

    public void stop() {
        audioPlayer.stop();
    }

    public long getCurrentPlaybackTime() {
        return audioPlayer.getCurrentPlaybackTime();
    }

    public void setPlaybackSpeed(float speed) {
        audioPlayer.setPlaybackSpeed(speed);
    }

    public interface OnSongEndCallback {
        void onSongEnd() throws IOException; // Allow IOException to be thrown
    }
}