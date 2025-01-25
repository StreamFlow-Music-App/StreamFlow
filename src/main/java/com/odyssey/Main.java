package com.odyssey;

import com.odyssey.components.CommandHandler;
import com.odyssey.components.PlaylistManager;
import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.components.StateManager;
import com.odyssey.services.PlaylistService;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (!authenticateUser()) {
            System.out.println("Invalid credentials. Exiting application.");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            String baseDirectory = "src/resources/playlists";
            String initialPlaylist = "songs";
            String currentDirectory = baseDirectory + "/" + initialPlaylist;

            List<String> songs = FileLoader.loadSongsFromFolder(currentDirectory);
            MainController mainController = new MainController(songs);
            PlaylistService playlistService = new PlaylistService();
            StateManager stateManager = new StateManager(songs);

            mainController.start();

            CommandHandler commandHandler = new CommandHandler(baseDirectory, mainController, playlistService);
            PlaylistManager playlistManager = new PlaylistManager(baseDirectory);

            while (true) {
                displayCommands(stateManager);

                String input = scanner.nextLine();
                if (input.startsWith("speed")) {
                    handleSpeedCommand(stateManager, input);
                } else {
                    commandHandler.handleCommand(input, playlistManager, currentDirectory);
                }

                if (input.isEmpty()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }


    private static void displayCommands(StateManager stateManager) {
        System.out.println("Next Song -> 'n'");
        System.out.println("Previous Song -> 'b'");
        System.out.println("Pause -> 'p'");
        System.out.println("Resume -> 'r'");
        System.out.println("Search song -> 's'");
        System.out.println("Shuffle Play -> 'c'");
        System.out.println("Set Playback Speed -> 'speed [value]'");
        System.out.println("Current Playback Speed: " + stateManager.getPlaybackSpeed() + "x");
        System.out.println("Shuffle song -> 'shuf'");
        System.out.println("Sort song -> 'sort" + "'");
        System.out.println("Available Commands For Playlist:");
        System.out.println("create [playlist_name] - Create a new playlist");
        System.out.println("delete [playlist_name] - Delete an existing playlist");
        System.out.println("switch [playlist_name] - Switch to a different playlist");
        System.out.println("add song - Add a song to the current playlist");
        System.out.println("remove song - Remove a song from the current playlist");
        System.out.println("view playlist - View songs in the current playlist");
        System.out.println("s - Search for a song and play it");
        System.out.println("c - Toggle shuffle play");
        System.out.println("stop - Stop the current playback");
    }

    private static void handleSpeedCommand(StateManager stateManager, String input) {
        try {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                double speed = Double.parseDouble(parts[1]);
                System.out.println("Attempting to set playback speed to: " + speed + "x");
                stateManager.setPlaybackSpeed(speed);
            } else {
                System.out.println("Invalid command. Use 'speed [value]'");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid speed value. Please provide a number between 0.5 and 2.0.");
        }
    }

    private static boolean authenticateUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return authenticate(username, password);
    }

    private static boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("login.txt"))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username) && credentials[1].trim().equals(password)) {
                    return true;
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }

        return false;
    }
}


