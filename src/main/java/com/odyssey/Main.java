package com.odyssey;

import com.odyssey.components.utils.FileUtils;
import com.odyssey.components.CommandHandler;
import com.odyssey.controllers.MainController;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String baseDirectory = "src/resources/playlists";
            String initialPlaylist = "songs";
            String currentDirectory = baseDirectory + "/" + initialPlaylist;

            List<String> songs = FileUtils.loadSongsFromFolder(currentDirectory);
            MainController mainController = new MainController(songs);
            mainController.start();

            CommandHandler commandHandler = new CommandHandler(baseDirectory, mainController);

            while (true) {
                displayCommands();
                String input = scanner.nextLine();
                commandHandler.handleCommand(input);
                if (input.isEmpty()) {
                    break;  // Exit the loop if input is empty
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void displayCommands() {
        System.out.println("Pause -> 'p'");
        System.out.println("Resume -> 'r'");
        System.out.println("Next Song -> 'n'");
        System.out.println("Previous Song -> 'b'");
        System.out.println("Create a new Playlist -> 'create [playlist name]'");
        System.out.println("Delete a Playlist -> 'delete [playlist name]'");
        System.out.println("Switch Playlist -> 'switch [playlist name]'");
        System.out.println("Stop -> Press Enter");
    }
}
