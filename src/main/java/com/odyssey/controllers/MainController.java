package com.odyssey.controllers;

import com.odyssey.components.StateManager;
import com.odyssey.services.ShuffleService;
import com.odyssey.components.SongFilter;
import com.odyssey.services.HistoryService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class MainController {
    private final PlayerController playerController;
    private final HistoryService historyService;
    private final SongFilter songFilter;
    private List<String> songs;
    private int currentIndex;
    private boolean hasSongs;
    private final StateManager stateManager;
    private String currentSongPath;
    private long playbackPosition;
    private ShuffleService shuffleService = new ShuffleService(); // Initialize ShuffleService
    private boolean isShuffleEnabled = false;

    public MainController(List<String> songs, HistoryService historyService) {
        this.songs = songs;
        this.songFilter = new SongFilter();
        this.currentIndex = 0;
        this.hasSongs = !songs.isEmpty();
        this.shuffleService = new ShuffleService();
        this.isShuffleEnabled = false;
        this.playerController = new PlayerController(this::playNextSong); // Use method reference
        this.historyService = historyService;
        this.stateManager = new StateManager();
        this.currentSongPath = null; // Initialize to null
        this.playbackPosition = 0;
        loadState();
    }

    private void loadState() {
        var state = stateManager.loadState();
        if (state != null && !state.isEmpty()) {
            currentSongPath = state.getProperty("currentSongPath");
            playbackPosition = Long.parseLong(state.getProperty("playbackPosition", "0"));
            System.out.println("Loaded state: Song = " + currentSongPath + ", Position = " + playbackPosition + "s");

            // Find the index of the saved song in the playlist
            if (currentSongPath != null) {
                currentIndex = songs.indexOf(currentSongPath);
                if (currentIndex == -1) {
                    System.out.println("Saved song not found in the current playlist. Starting fresh.");
                    currentSongPath = null;
                    playbackPosition = 0;
                }
            }
        } else {
            System.out.println("No saved state found. Starting fresh.");
        }
    }

    // Save the current state
    public void saveState() {
        if (currentSongPath != null) {
            long currentPlaybackTime = playerController.getCurrentPlaybackTime();
            stateManager.saveState(currentSongPath, currentPlaybackTime);
        } else {
            System.out.println("No song is currently playing. State not saved.");
        }
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
            case "reset": // Handle the reset command
                resetState();
                playCurrentSong();
            default:
                System.out.println("Invalid command.");
        }
    }

    public void playCurrentSong() throws IOException {
        currentSongPath = songs.get(currentIndex); // Update currentSongPath
        System.out.println("Now playing: " + currentSongPath); // Debug log
        historyService.addToHistory(currentSongPath);

        // Extract song name for display
        String[] songParts = currentSongPath.split("/");
        String rawSongName = songParts[songParts.length - 1];
        String songName = rawSongName.contains("_") ? rawSongName.substring(rawSongName.indexOf("_") + 1) : rawSongName;
        String albumName = (songParts.length > 3) ? songParts[3] : "Unknown Album";

        System.out.println();
        System.out.println();
        System.out.println("You are on: " + albumName);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Playing song: " + songName);
        System.out.println("---------------------------------------------------------------");

        playerController.play(currentSongPath);
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
        if (currentIndex > 0) {
            currentIndex--; // Move to the previous song
            if (isShuffleEnabled) {
                // If shuffle is enabled, get the previous shuffled index
                currentIndex = shuffleService.getPreviousShuffledIndex();
            }
            playCurrentSong(); // Play the previous song
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

    public boolean searchAndPlaySong(String searchTerm) {
        List<String> matchingSongs = new ArrayList<>();

        // Find all songs that match the search term (case-insensitive and partial match)
        for (int i = 0; i < songs.size(); i++) {
            String path = songs.get(i);
            String fileName = Paths.get(path).getFileName().toString();

            // Check if the file name contains the search term (case-insensitive)
            if (fileName.toLowerCase().contains(searchTerm.toLowerCase())) {
                matchingSongs.add(fileName);
            }
        }

        // If no matches found
        if (matchingSongs.isEmpty()) {
            System.out.println("No songs found matching: " + searchTerm);
            return false;
        }

        // Display all matching songs
        System.out.println("Matching songs:");
        for (int i = 0; i < matchingSongs.size(); i++) {
            System.out.println((i + 1) + ": " + matchingSongs.get(i));
        }

        // Let the user choose which song to play
        System.out.print("Enter the number of the song you want to play: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        // Validate the user's choice
        if (choice > 0 && choice <= matchingSongs.size()) {
            String selectedSong = matchingSongs.get(choice - 1);
            for (int i = 0; i < songs.size(); i++) {
                String path = songs.get(i);
                String fileName = Paths.get(path).getFileName().toString();

                if (fileName.equals(selectedSong)) {
                    currentIndex = i;
                    try {
                        playCurrentSong();
                    } catch (IOException e) {
                        System.err.println("Error playing the song: " + e.getMessage());
                    }
                    return true;
                }
            }
        } else {
            System.out.println("Invalid choice.");
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

    public void handleFilterCommand(String filterType, String filterValue) {
        List<String> filteredSongs;

        switch (filterType.toLowerCase()) {
            case "artist":
                filteredSongs = songFilter.filterByArtist(songs, filterValue);
                break;
            case "song":
                filteredSongs = songFilter.filterBySongName(songs, filterValue);
                break;
            default:
                System.out.println("Invalid filter type. Use 'artist' or 'song'.");
                return;
        }


        if (filteredSongs.isEmpty()) {
            System.out.println("No songs found matching the filter.");
        } else {
            System.out.println("Filtered songs:");
            for (String songPath : filteredSongs) {
                String[] parts = songPath.split("/");
                String songName = parts[parts.length - 1]; // Extract the song name
                System.out.println(songName);
            }
        }
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




    // Reset the state
    public void resetState() throws IOException {
        System.out.println("Resetting state...");
        stopCurrentSong();
        stateManager.resetState();
        currentSongPath = null;
        playbackPosition = 0;
        currentIndex = 0;
        System.out.println("State reset. Playback will start from the beginning.");
        if (hasSongs) {
            playCurrentSong();
        } else {
            System.out.println("No songs available in the playlist.");
        }
    }
    }










