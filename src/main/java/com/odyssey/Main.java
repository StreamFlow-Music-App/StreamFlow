package com.odyssey;

import com.odyssey.components.CommandHandler;
import com.odyssey.components.PlaylistManager;
import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.services.HistoryService;
import com.odyssey.services.PlaylistService;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Odyssey Music Player!");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Choose an option (1/2): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        boolean authenticated = false;

        if (choice == 1) {
            authenticated = authenticateUser(scanner);
        } else if (choice == 2) {
            registerUser(scanner);
            System.out.println("Registration successful! Please log in.");
            authenticated = authenticateUser(scanner);
        } else {
            System.out.println("Invalid choice. Exiting...");
            return;
        }

        if (!authenticated) {
            System.out.println("Invalid credentials. Exiting application.");
            return;
        }

        try {
            String baseDirectory = "src/resources/playlists";
            String initialPlaylist = "songs";
            String currentDirectory = baseDirectory + "/" + initialPlaylist;

            List<String> songs = FileLoader.loadSongsFromFolder(currentDirectory);
            HistoryService historyService = new HistoryService();
            MainController mainController = new MainController(songs, historyService);
            PlaylistService playlistService = new PlaylistService();
            mainController.start();

            CommandHandler commandHandler = new CommandHandler(baseDirectory, mainController, playlistService);
            PlaylistManager playlistManager = new PlaylistManager(baseDirectory);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (mainController.getCurrentSongPath() != null) {
                    mainController.saveState();
                    System.out.println("State saved successfully.");
                } else {
                    System.out.println("No song is playing. State not saved.");
                }
            }));

            while (true) {
                String directory = commandHandler.getNewDirectory();
                displayCommands(directory);

                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("reset")) {
                    mainController.resetState();
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

    private static void displayCommands(String directory) {
        System.out.println("\n-----------------------------");
        System.out.println("Available Commands:");
        System.out.println("-----------------------------");
        System.out.println("Next Song -> 'n'");
        System.out.println("Previous Song -> 'b'");
        System.out.println("Pause -> 'p'");
        System.out.println("Resume -> 'r'");
        System.out.println("Search song -> 's'");
        System.out.println("Shuffle Play -> 'c'");
        System.out.println("Add to Favourites -> 'f'");
        System.out.println("List Favourites -> 'l'");
        System.out.println("Show Playback Time -> 't'");
        System.out.println("Set Playback Speed -> 'speed'");
        System.out.println("Show History -> 'h'");
        System.out.println("Filter Songs -> 'filter [artist/song] [value]'");
        System.out.println("Sort Songs -> 'sort [title/artist/album]'");
        System.out.println("Add song to playlist -> 'add song'");
        System.out.println("Remove song from playlist -> 'remove song'");

        if (!Objects.equals(directory, null) && !Objects.equals(directory, "src/resources/playlists/songs")) {
            System.out.println("Add song -> 'add song'");
            System.out.println("Remove song -> 'remove song'");
        }

        System.out.println("Create a new Playlist -> 'create [playlist name]'");
        System.out.println("Delete a Playlist -> 'delete [playlist name]'");
        System.out.println("Switch Playlist -> 'switch [playlist name]'");
        System.out.println("Reset State -> 'reset'");
        System.out.println("Stop -> Press Enter");
        System.out.println("-----------------------------\n");
    }

    private static boolean authenticateUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return authenticate(username, password);
    }

    private static boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/login.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username) && credentials[1].trim().equals(password)) {
                    System.out.println("Login successful! Welcome, " + username);
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }
        return false;
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine();

        if (isUserExists(username)) {
            System.out.println("Username already taken. Try another.");
            return;
        }

        try (FileWriter writer = new FileWriter("src/resources/login.txt", true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(username + "," + password + ",false\n");
            System.out.println("Account created successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to login file: " + e.getMessage());
        }
    }

    private static boolean isUserExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/login.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }
        return false;
    }
}
