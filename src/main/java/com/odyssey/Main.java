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

        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome! Choose an option:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Enter your choice: ");
        String choice = sc.nextLine();

        boolean authenticated = false;

        if (choice.equals("1")) {
            authenticated = authenticateUser();
        } else if (choice.equals("2")) {
            registerUser();
            return;
        }else{
            System.out.println("Invalid option. Exiting...");
            return;
        }


        if (!authenticated) {
            System.out.println("Invalid credentials. Exiting application.");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            String baseDirectory = "src/resources/playlists";
            String initialPlaylist = "songs";
            String currentDirectory = baseDirectory + "/" + initialPlaylist;

            List<String> songs = FileLoader.loadSongsFromFolder(currentDirectory);
            HistoryService historyService = new HistoryService();
            MainController mainController = new MainController(songs,historyService);
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

    private static void displayCommands(String directory) {
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

        if (!Objects.equals(directory, null) && !Objects.equals(directory, "src/resources/playlists/songs")) {
            System.out.println("Add song -> 'add song'");
            System.out.println("Remove song -> 'remove song'");
        }

        System.out.println("Create a new Playlist -> 'create [playlist name]'");
        System.out.println("Delete a Playlist -> 'delete [playlist name]'");
        System.out.println("Switch Playlist -> 'switch [playlist name]'");
        System.out.println("Stop -> Press Enter");
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


    private static void registerUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter a new password: ");
        String password = scanner.nextLine();

        if (isUserExists(username)) {
            System.out.println("Username already exists. Try a different one.");
            return;
        }

        try (FileWriter writer = new FileWriter("src/main/resources/login.txt", true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(username + "," + password + ",false");
            bufferedWriter.newLine();
            System.out.println("Registration successful! You can now log in.");
        } catch (IOException e) {
            System.err.println("Error writing to login file: " + e.getMessage());
        }
    }

    private static boolean isUserExists(String username) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("login.txt"))))) {
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