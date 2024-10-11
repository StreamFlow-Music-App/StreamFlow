package com.odyssey.controllers;

import java.io.IOException;
import java.util.List;

public class MainController {
    private final PlayerController playerController;
    private final List<String> songs;
    private int currentIndex;

    public MainController(List<String> songs) {
        this.songs = songs;
        this.currentIndex = 0;
        this.playerController = new PlayerController(() -> {
            try {
                playNextSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    public void start() throws IOException {
        playCurrentSong();
    }

    public void handleInput(String input) throws IOException {
        switch (input.toLowerCase()) {
            case "p":
                playerController.pause();
                System.out.println("Song paused.");
                break;
            case "r":
                playerController.resume();
                System.out.println("Song resumed.");
                break;
            case "n":
                playNextSong();
                break;
            case "b":
                playPreviousSong();
                break;
            default:
                playerController.stop();
                System.out.println("Song stopped.");
                break;
        }
    }

    private void playCurrentSong() throws IOException {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Playing song: " + songs.get(currentIndex).split("/")[3]);
        System.out.println("-----------------------------------------------------------------------------");
        playerController.play(songs.get(currentIndex));
    }

    private void playNextSong() throws IOException {
        if (currentIndex < songs.size() - 1) {
            currentIndex++;
            playCurrentSong();
        } else {
            System.out.println("You are at the last song. No next song available.");
        }
    }

    private void playPreviousSong() throws IOException {
        if (currentIndex > 0) {
            currentIndex--;
            playCurrentSong();
        } else {
            System.out.println("You are at the first song. No previous song available.");
        }
    }
}
