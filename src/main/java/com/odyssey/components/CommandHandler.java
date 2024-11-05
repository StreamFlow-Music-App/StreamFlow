package com.odyssey.components;

import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.services.PlaylistService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {
    private final MainController mainController;
    private final PlaylistService playlistService;
    private final String baseDirectory;
    private String newDirectory;

    public CommandHandler(String baseDirectory, MainController mainController, PlaylistService playlistService) {
        this.baseDirectory = baseDirectory;
        this.mainController = mainController;
        this.playlistService = playlistService;
    }

    // CommandHandler.java

    public void handleCommand(String input, PlaylistManager playlistManager, String currentDirectory) {
        try {
            if (input.isEmpty()) {
                mainController.handleInput("stop");
            } else if (input.startsWith("create ")) {
                handleCreateCommand(input, playlistManager);
            } else if (input.startsWith("delete ")) {
                handleDeleteCommand(input, playlistManager);
            } else if (input.startsWith("switch ")) {
                handleSwitchCommand(input);
            } else if (input.equals("add song")) {
                handleAddSong(currentDirectory);
            } else if (input.equals("remove song")) {
                handleRemoveSong();
            } else if (input.equals("s")) {  // Check if the command is for searching
                handleSearchCommand();
            } else {
                mainController.handleInput(input);
            }
        } catch (IOException e) {
            System.err.println("Error processing command: " + e.getMessage());
        }
    }

    private void handleSearchCommand() {
        System.out.print("Enter song name to search [Song name - Artist name] : " );
        String songName = new java.util.Scanner(System.in).nextLine();

        if (mainController.searchAndPlaySong(songName)) {
            System.out.println("Playing searched song: " + songName);
        } else {
            System.out.println("Song not available.");
        }
    }

    private void handleAddSong(String currentDirectory) throws IOException {
        List<Path> availableSongs = Files.list(Paths.get(currentDirectory))
                .collect(Collectors.toList());

        System.out.println("Available songs:");
        for (int i = 0; i < availableSongs.size(); i++) {
            System.out.println(i + ": " + availableSongs.get(i).getFileName());
        }

        System.out.print("Enter the index of the song you want to add: ");
        int songIndex = Integer.parseInt(new java.util.Scanner(System.in).nextLine());

        if (songIndex >= 0 && songIndex < availableSongs.size()) {
            Path sourceDirectory = availableSongs.get(songIndex);
            Path destinationDirectory = Paths.get(newDirectory, sourceDirectory.getFileName().toString());

            playlistService.addSong(destinationDirectory.toString(), sourceDirectory.toString());
            List<String> songs = FileLoader.loadSongsFromFolder(newDirectory);
            mainController.setSongs(songs);
        } else {
            System.out.println("Invalid index. Please try again.");
        }
    }

    private void handleRemoveSong() throws IOException {
        List<Path> currentPlaylistSongs = Files.list(Paths.get(newDirectory))
                .collect(Collectors.toList());

        System.out.println("Current songs in the playlist:");
        for (int i = 0; i < currentPlaylistSongs.size(); i++) {
            System.out.println(i + ": " + currentPlaylistSongs.get(i).getFileName());
        }

        System.out.print("Enter the index of the song you want to remove: ");
        int removeIndex = Integer.parseInt(new java.util.Scanner(System.in).nextLine());

        if (removeIndex >= 0 && removeIndex < currentPlaylistSongs.size()) {
            Path songToRemove = currentPlaylistSongs.get(removeIndex);

            playlistService.removeSong(songToRemove.toString());
            List<String> songs = FileLoader.loadSongsFromFolder(newDirectory);
            mainController.setSongs(songs);
        } else {
            System.out.println("Invalid index. Please try again.");
        }
    }

    private void handleCreateCommand(String input, PlaylistManager playlistManager) {
        String[] commandParts = input.split(" ", 2);
        if (commandParts.length == 2) {
            String newPlaylist = commandParts[1];
            playlistManager.createNewPlaylist(newPlaylist);
        } else {
            System.out.println("Invalid create command. Use 'create [playlist name]'.");
        }
    }

    private void handleDeleteCommand(String input, PlaylistManager playlistManager) {
        String[] commandParts = input.split(" ", 2);
        if (commandParts.length == 2) {
            String playlistToDelete = commandParts[1];
            playlistManager.deletePlaylist(playlistToDelete);
        } else {
            System.out.println("Invalid delete command. Use 'delete [playlist name]'.");
        }
    }

    private void handleSwitchCommand(String input) {
        String[] commandParts = input.split(" ", 2);
        if (commandParts.length == 2) {
            String newPlaylist = commandParts[1];
            newDirectory = baseDirectory + "/" + newPlaylist;

            if (Files.exists(Paths.get(newDirectory))) {
                List<String> newSongs = FileLoader.loadSongsFromFolder(newDirectory);
                mainController.stopCurrentSong();

                mainController.setSongs(newSongs);
                try {
                    mainController.start();
                } catch (IOException e) {
                    System.err.println("Error starting playback: " + e.getMessage());
                }
            } else {
                System.out.println("Playlist '" + newPlaylist + "' does not exist.");
            }
        } else {
            System.out.println("Invalid switch command. Use 'switch [playlist name]'.");
        }
    }

    public String getNewDirectory() {
        return newDirectory;
    }
}