package com.odyssey.controllers;

import com.odyssey.components.Song;
import com.odyssey.services.ShuffleService;
import com.odyssey.components.SongFilter;
import com.odyssey.services.HistoryService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainController {
    private final PlayerController playerController;
    private final ShuffleService shuffleService;
    private final HistoryService historyService;
    private List<String> songs;
    private int currentIndex;
    private boolean hasSongs;
    private boolean isShuffleEnabled;

    public MainController(List<String> songs, HistoryService historyService) {
        this.songs = songs;
        this.currentIndex = 0;
        this.hasSongs = !songs.isEmpty();
        this.shuffleService = new ShuffleService();
        this.isShuffleEnabled = false;
        this.playerController = new PlayerController(this::playNextSong); // Use method reference
        this.historyService = historyService;
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

    public void toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled;
        if (isShuffleEnabled) {
            shuffleService.initializeShuffle(songs.size());
            System.out.println("Shuffle mode is ON.");
        } else {
            System.out.println("Shuffle mode is OFF.");
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
            case "t":
                displayPlaybackTime();
                break;
            case "speed":
                setPlaybackSpeed();
                break;
            default:
                System.out.println("Invalid command.");
        }
    }

    private void playCurrentSong() throws IOException {
        String songPath = songs.get(currentIndex);
        historyService.addToHistory(songPath); // Add the current song to history


        String[] songParts = songPath.split("/");

        String rawSongName = songParts[songParts.length - 1];
        String songName = rawSongName.contains("_") ? rawSongName.substring(rawSongName.indexOf("_") + 1) : rawSongName;

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
        if (isShuffleEnabled) {
            currentIndex = shuffleService.getNextShuffledIndex();
        } else {
            if (currentIndex < songs.size() - 1) {
                currentIndex++;
            } else {
                System.out.println("You are at the last song. No next song available.");
                return;
            }
        }
        playCurrentSong();
    }

    private void playPreviousSong() throws IOException {
        if (isShuffleEnabled && currentIndex > 0) {
            currentIndex--;
        } else {
            System.out.println("You are at the first song. No previous song available.");
        }
        playCurrentSong();
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
                return true;
            }
        }
        return false;
    }

    public String getCurrentSongPath() {
        if (hasSongs && currentIndex >= 0 && currentIndex < songs.size()) {
            return songs.get(currentIndex);
        }
        return null;
    }

    private void displayPlaybackTime() {
        long currentTime = playerController.getCurrentPlaybackTime();
        System.out.println("Current playback time: " + currentTime + " seconds");
    }

    private void setPlaybackSpeed() {
        System.out.print("Enter playback speed (e.g., 1.0 for normal, 1.5 for 1.5x): ");
        Scanner scanner = new Scanner(System.in);
        float speed = scanner.nextFloat();
        playerController.setPlaybackSpeed(speed);
    }

    public void showHistory() {
        ArrayList<String> history = historyService.getHistory();
        if (history.isEmpty()) {
            System.out.println("No songs have been played yet.");
        } else {
            System.out.println("Playback History:");
            for (int i = 0; i < history.size(); i++) {
                System.out.println((i + 1) + ": " + history.get(i));
            }
        }
    }



    }



