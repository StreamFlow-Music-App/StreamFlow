package com.odyssey.components;

import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.services.PlaylistService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandHandler {
    private final MainController mainController;
    private final PlaylistService playlistService;
    private final FavouriteManager favouriteManager;
    private final String baseDirectory;
    private String newDirectory;

    public CommandHandler(String baseDirectory, MainController mainController, PlaylistService playlistService) {
        this.baseDirectory = baseDirectory;
        this.mainController = mainController;
        this.playlistService = playlistService;
        this.favouriteManager = new FavouriteManager(baseDirectory);
    }

    public void handleCommand(String input, PlaylistManager playlistManager, String currentDirectory, String username) {
        try {
            if (input.isEmpty()) {
                mainController.handleInput("stop");
            } else if (input.startsWith("create ")) {
                handleCreateCommand(input, playlistManager);
            }else if (input.equalsIgnoreCase("h")) {
                mainController.showHistory();
            } else if (input.startsWith("delete ")) {
                handleDeleteCommand(input, playlistManager);
            } else if (input.startsWith("switch ")) {
                handleSwitchCommand(input);
            } else if (input.equals("add song")) {
                handleAddSong(currentDirectory);
            } else if (input.equals("remove song")) {
                handleRemoveSong();
            } else if (input.equals("s")) {
                handleSearchCommand();
            } else if (input.equalsIgnoreCase("c")) {
                mainController.toggleShuffle();
            } else if (input.equalsIgnoreCase("f")) {
                handleFavouriteCommand();
            } else if (input.equalsIgnoreCase("l")) {
                handleListFavouritesCommand();
            }else if(input.equalsIgnoreCase("m")){
                handleMembership(username);
            }else {
                mainController.handleInput(input);
            }
        } catch (IOException e) {
            System.err.println("Error processing command: " + e.getMessage());
        }
    }

    private void handleFavouriteCommand() throws IOException {
        String currentSongPath = mainController.getCurrentSongPath();
        if (currentSongPath != null) {
            favouriteManager.addSongToFavourites(currentSongPath);
            System.out.println("Song added to favourites.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }

    private void handleListFavouritesCommand() throws IOException {
        List<String> favouriteSongs = favouriteManager.getFavouriteSongs();
        if (favouriteSongs.isEmpty()) {
            System.out.println("No songs in favourites.");
        } else {
            System.out.println("Favourite songs:");
            for (String song : favouriteSongs) {
                System.out.println(song);
            }
        }


    }

    private void handleSearchCommand() {
        System.out.print("Enter song name to search [Song name - Artist name] : ");
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
                newDirectory = null;
            }
        } else {
            System.out.println("Invalid switch command. Use 'switch [playlist name]'.");
        }
    }


    private static final String LOGIN_FILE = "src/main/resources/login.txt";

    public void handleMembership(String username) {
        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");

                if (credentials.length == 3 && credentials[0].trim().equals(username)) {
                    if (credentials[2].trim().equals("false")) {
                        // Prompt for payment details
                        if (collectPaymentDetails()) {
                            credentials[2] = "true"; // Update false to true
                            userFound = true;
                        } else {
                            System.out.println("Payment failed. Membership not updated.");
                            return;
                        }
                    }
                }

                updatedLines.add(String.join(",", credentials));
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
            return;
        }

        if (!userFound) {
            System.out.println("Username not found or already a member.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOGIN_FILE))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("Membership status updated successfully!");
        } catch (IOException e) {
            System.err.println("Error writing to login file: " + e.getMessage());
        }
    }

    private boolean collectPaymentDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Credit Card Number (16 digits): ");
        String cardNumber = scanner.nextLine();
        if (!Pattern.matches("\\d{16}", cardNumber)) {
            System.out.println("Invalid card number format.");
            return false;
        }

        System.out.print("Enter Expiry Date (MM/YY): ");
        String expiryDate = scanner.nextLine();
        if (!Pattern.matches("(0[1-9]|1[0-2])/(\\d{2})", expiryDate)) {
            System.out.println("Invalid expiry date format.");
            return false;
        }

        System.out.print("Enter CVC (3 digits): ");
        String cvc = scanner.nextLine();
        if (!Pattern.matches("\\d{3}", cvc)) {
            System.out.println("Invalid CVC format.");
            return false;
        }

        System.out.println("Payment successful!");
        return true;
    }


    public String getNewDirectory() {
        return newDirectory;
    }
}
