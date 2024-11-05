package com.odyssey;

import com.odyssey.components.CommandHandler;
import com.odyssey.components.PlaylistManager;
import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.services.PlaylistService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String baseDirectory = "src/resources/playlists";
            String initialPlaylist = "songs";
            String currentDirectory = baseDirectory + "/" + initialPlaylist;

            List<String> songs = FileLoader.loadSongsFromFolder(currentDirectory);
            MainController mainController = new MainController(songs);
            PlaylistService playlistService = new PlaylistService();
            mainController.start();

            CommandHandler commandHandler = new CommandHandler(baseDirectory, mainController, playlistService);
            PlaylistManager playlistManager = new PlaylistManager(baseDirectory);

            while (true) {
                String directory = commandHandler.getNewDirectory();
                displayCommands(directory);

                String input = scanner.nextLine();
                commandHandler.handleCommand(input, playlistManager, currentDirectory);

                if (input.isEmpty()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    // Main.java

    private static void displayCommands(String directory) {
        System.out.println("Next Song -> 'n'");
        System.out.println("Previous Song -> 'b'");
        System.out.println("Pause -> 'p'");
        System.out.println("Resume -> 'r'");
        System.out.println("Search song -> 's'");  // Add search option

        if (!Objects.equals(directory, null) && !Objects.equals(directory, "src/resources/playlists/songs")) {
            System.out.println("Add song -> 'add song'");
            System.out.println("Remove song -> 'remove song'");
        }

        System.out.println("Create a new Playlist -> 'create [playlist name]'");
        System.out.println("Delete a Playlist -> 'delete [playlist name]'");
        System.out.println("Switch Playlist -> 'switch [playlist name]'");
        System.out.println("Stop -> Press Enter");
    }

}