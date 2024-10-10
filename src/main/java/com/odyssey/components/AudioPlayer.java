package com.odyssey.components;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {
    private Player player;
    private String currentSongPath;

    public void play(String songPath) {
        try {
            stop();

            currentSongPath = songPath;
            FileInputStream fileInputStream = new FileInputStream(currentSongPath);
            player = new Player(fileInputStream);
            new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
    }

    public String getCurrentSongPath() {
        return currentSongPath;
    }
}
