package com.odyssey.controllers;

import com.odyssey.components.AudioPlayer;

import java.io.IOException;

public class PlayerController {
    private final AudioPlayer audioPlayer;
    private final OnSongEndCallback onSongEndCallback;

    public PlayerController(OnSongEndCallback callBack) {
        this.audioPlayer = new AudioPlayer();
        this.onSongEndCallback = callBack;

        audioPlayer.setOnSongEndListener(()->{
            if(onSongEndCallback != null){
                onSongEndCallback.onSongEnd();
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

    public void setPlaybackSpeed(float speed) {
        audioPlayer.setPlaybackSpeed(speed); // Pass the speed to AudioPlayer
    }

    public interface OnSongEndCallback{
        void onSongEnd();
    }
}
