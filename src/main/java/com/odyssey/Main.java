package com.odyssey;

import com.odyssey.components.CommandHandler;
import com.odyssey.components.PlaylistManager;
import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.services.HistoryService;
import com.odyssey.services.PlaylistService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        System.out.println("Filter Song -> 'filter [artist/song] [value]'");
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
}
