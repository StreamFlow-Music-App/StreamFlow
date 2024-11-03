package com.odyssey;

import com.odyssey.components.CommandHandler;
import com.odyssey.components.PlaylistManager;
import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;

import java.nio.file.*;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String baseDirectory = "src/resources/playlists";
            String initialPlaylist = "songs";
            String currentDirectory = baseDirectory + "/" + initialPlaylist;

            List<String> songs = FileLoader.loadSongsFromFolder(currentDirectory);
            MainController mainController = new MainController(songs);
            mainController.start();

            CommandHandler commandHandler = new CommandHandler(baseDirectory, mainController);
            PlaylistManager playlistManager = new PlaylistManager(baseDirectory);

            while (true) {
                String directory = commandHandler.getNewDirectory();
                displayCommands(directory);

                String input = scanner.nextLine();
                commandHandler.handleCommand(input, playlistManager);

                if (input.equals("add")) {
                    // Display songs from the source directory
                    List<Path> availableSongs = Files.list(Paths.get(currentDirectory))
                            .collect(Collectors.toList());
                    System.out.println("Available songs:");
                    for (int i = 0; i < availableSongs.size(); i++) {
                        System.out.println(i + ": " + availableSongs.get(i).getFileName());
                    }

                    // Ask user for the index of the song to add
                    System.out.print("Enter the index of the song you want to add: ");
                    int songIndex = Integer.parseInt(scanner.nextLine());

                    if (songIndex >= 0 && songIndex < availableSongs.size()) {
                        Path sourceDirectory = availableSongs.get(songIndex);
                        Path destinationDirectory = Paths.get(directory, sourceDirectory.getFileName().toString());

                        System.out.println("src: " + sourceDirectory);
                        System.out.println("des: " + destinationDirectory);
                        addSongToPlaylist(sourceDirectory, destinationDirectory);
                        songs = FileLoader.loadSongsFromFolder(currentDirectory); // Reload songs after adding
                        mainController.setSongs(songs); // Update the MainController with the new song list
                    } else {
                        System.out.println("Invalid index. Please try again.");
                    }
                }

                // New feature to remove a song from the playlist
                if (input.equals("remove")) {
                    // Display current songs in the playlist
                    List<Path> currentPlaylistSongs = Files.list(Paths.get(directory))
                            .collect(Collectors.toList());
                    System.out.println("Current songs in the playlist:");
                    for (int i = 0; i < currentPlaylistSongs.size(); i++) {
                        System.out.println(i + ": " + currentPlaylistSongs.get(i).getFileName());
                    }

                    // Ask user for the index of the song to remove
                    System.out.print("Enter the index of the song you want to remove: ");
                    int removeIndex = Integer.parseInt(scanner.nextLine());

                    if (removeIndex >= 0 && removeIndex < currentPlaylistSongs.size()) {
                        Path songToRemove = currentPlaylistSongs.get(removeIndex);
                        removeSongFromPlaylist(songToRemove);
                        songs = FileLoader.loadSongsFromFolder(currentDirectory); // Reload songs after removal
                        mainController.setSongs(songs); // Update the MainController with the new song list
                    } else {
                        System.out.println("Invalid index. Please try again.");
                    }
                }

                if (input.isEmpty()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void addSongToPlaylist(Path source, Path destination) {
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Song added successfully!");
        } catch (Exception e) {
            System.out.println("Error occurred while adding the song.");
            e.printStackTrace();
        }
    }

    private static void removeSongFromPlaylist(Path songPath) {
        try {
            Files.delete(songPath);
            System.out.println("Song removed successfully!");
        } catch (IOException e) {
            System.out.println("Error occurred while removing the song.");
            e.printStackTrace();
        }
    }

    private static void displayCommands(String directory) {
        System.out.println("Next Song -> 'n'");
        System.out.println("Previous Song -> 'b'");
        System.out.println("Pause -> 'p'");
        System.out.println("Resume -> 'r'");

        if (!Objects.equals(directory, null) && !Objects.equals(directory, "src/resources/playlists/songs")) {
            System.out.println("Add song -> 'add'");
            System.out.println("Remove song -> 'remove'");
        }

        System.out.println("Create a new Playlist -> 'create [playlist name]'");
        System.out.println("Delete a Playlist -> 'delete [playlist name]'");
        System.out.println("Switch Playlist -> 'switch [playlist name]'");
        System.out.println("Stop -> Press Enter");
    }
}
