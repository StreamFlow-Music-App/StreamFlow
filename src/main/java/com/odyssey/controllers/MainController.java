package com.odyssey.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class MainController {
    private final PlayerController playerController;
    private List<String> songs;
    private int currentIndex;
    private boolean hasSongs;

    public MainController(List<String> songs) {
        this.songs = songs;
        this.currentIndex = 0;
        this.hasSongs = !songs.isEmpty();
        this.playerController = new PlayerController(() -> {
            try {
                playNextSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setSongs(List<String> newSongs) {
        this.songs = newSongs;
        this.currentIndex = 0;
        this.hasSongs = !newSongs.isEmpty();
    }

    public void start() throws IOException {
        if (!hasSongs) {
            System.out.println("No songs available in the current playlist.");
        } else {
            playCurrentSong();
        }
    }

    public void handleInput(String input) throws IOException {
        if (!hasSongs) {
            System.out.println("No songs available. Please switch to a playlist with songs.");
            return;
        }

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
            case "stop":
                playerController.stop();
                System.out.println("Song stopped.");
                break;
            default:
                System.out.println("Invalid command.");
        }
    }

    private void playCurrentSong() throws IOException {
        String songPath = songs.get(currentIndex);
        String[] songParts = songPath.split("/");

        // Extract the song name from the last part of the path and remove any prefixes like "11086_"
        String rawSongName = songParts[songParts.length - 1];
        String songName = rawSongName.contains("_") ? rawSongName.substring(rawSongName.indexOf("_") + 1) : rawSongName;

        // Use album name if available in the path; otherwise, set it to "Unknown Album"
        String albumName = (songParts.length > 3) ? songParts[3] : "Unknown Album";

        System.out.println();
        System.out.println();
        System.out.println("You are on: " + albumName);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Playing song: " + songName);
        System.out.println("---------------------------------------------------------------");

        playerController.play(songPath);
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

    public void stopCurrentSong() {
        try {
            playerController.stop();
            System.out.println("Current song stopped.");
        } catch (Exception e) {
            System.err.println("Error stopping the current song: " + e.getMessage());
        }
    }

    public boolean searchAndPlaySong(String songName) {
        for (int i = 0; i < songs.size(); i++) {
            String path = songs.get(i);
            String fileName = Paths.get(path).getFileName().toString();

            if (fileName.equalsIgnoreCase(songName + ".mp3")) {
                currentIndex = i;
                try {
                    playCurrentSong();
                } catch (IOException e) {
                    System.err.println("Error playing the song: " + e.getMessage());
                }
                return true;  // Song found and played
            }
        }
        return false;  // Song not found
    }
}
