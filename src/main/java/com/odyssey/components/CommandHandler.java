package com.odyssey.components;

import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CommandHandler {
    private final MainController mainController;
    private final String baseDirectory; // Add baseDirectory field

    public CommandHandler(String baseDirectory, MainController mainController) {
        this.baseDirectory = baseDirectory;
        this.mainController = mainController;
    }

    public void handleCommand(String input, PlaylistManager playlistManager) {
        try {
            if (input.isEmpty()) {
                mainController.handleInput("stop");
            } else if (input.startsWith("create ")) {
                handleCreateCommand(input, playlistManager);
            } else if (input.startsWith("delete ")) {
                handleDeleteCommand(input, playlistManager);
            } else if (input.startsWith("switch ")) {
                handleSwitchCommand(input);
            } else {
                mainController.handleInput(input);
            }
        } catch (IOException e) {
            System.err.println("Error processing command: " + e.getMessage());
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
            String newDirectory = baseDirectory + "/" + newPlaylist;

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
}
