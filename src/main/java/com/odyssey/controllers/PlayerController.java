package com.odyssey.controllers;

import com.odyssey.components.AudioPlayer;

import java.io.IOException;

public class PlayerController {
    private final AudioPlayer audioPlayer;

    public PlayerController() {
        this.audioPlayer = new AudioPlayer();
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
}
