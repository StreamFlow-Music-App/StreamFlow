package com.odyssey.components;

import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.services.PlaylistService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
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

    public void handleCommand(String input, PlaylistManager playlistManager, String currentDirectory) {
        try {
            if (input.isEmpty()) {
                mainController.handleInput("stop");
            } else if (input.startsWith("create ")) {
                handleCreateCommand(input, playlistManager);
            }else if (input.startsWith("filter ")) {
                handleFilterCommand(input);
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
            } else {
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
        // List available playlists
        List<Path> playlists = Files.list(Paths.get("src/resources/playlists"))
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        System.out.println("\n-----------------------------");
        System.out.println("Available playlists:");
        for (int i = 0; i < playlists.size(); i++) {
            System.out.println(i + ": " + playlists.get(i).getFileName());
        }

        // Prompt the user to select a playlist
        System.out.print("Enter the index of the playlist: ");
        int playlistIndex = new Scanner(System.in).nextInt();

        if (playlistIndex >= 0 && playlistIndex < playlists.size()) {
            String playlistName = playlists.get(playlistIndex).getFileName().toString();

            // List available songs in the songs folder
            List<Path> availableSongs = Files.list(Paths.get("src/resources/playlists/songs"))
                    .collect(Collectors.toList());

            System.out.println("\n-----------------------------");
            System.out.println("Available songs:");
            for (int i = 0; i < availableSongs.size(); i++) {
                System.out.println(i + ": " + availableSongs.get(i).getFileName());
            }
            System.out.println("-----------------------------");
            // Prompt the user to select a song
            System.out.print("Enter the index of the song you want to add: ");
            int songIndex = new Scanner(System.in).nextInt();

            if (songIndex >= 0 && songIndex < availableSongs.size()) {
                Path selectedSong = availableSongs.get(songIndex);

                // Add the song to the selected playlist
                playlistService.addSongToPlaylist(playlistName, selectedSong.toString());

                // Display the updated playlist
                displayPlaylistContents(playlistName);
            } else {
                System.out.println("Invalid song index. Please try again.");
            }
        } else {
            System.out.println("Invalid playlist index. Please try again.");
        }
    }

    private void handleRemoveSong() throws IOException {
        // List available playlists
        List<Path> playlists = Files.list(Paths.get("src/resources/playlists"))
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        System.out.println("\n-----------------------------");
        System.out.println("Available playlists:");
        for (int i = 0; i < playlists.size(); i++) {
            System.out.println(i + ": " + playlists.get(i).getFileName());
        }

        // Prompt the user to select a playlist
        System.out.print("Enter the index of the playlist: ");
        int playlistIndex = new Scanner(System.in).nextInt();

        if (playlistIndex >= 0 && playlistIndex < playlists.size()) {
            String playlistName = playlists.get(playlistIndex).getFileName().toString();

            // List songs in the selected playlist
            List<Path> songsInPlaylist = Files.list(Paths.get("src/resources/playlists/" + playlistName))
                    .collect(Collectors.toList());

            System.out.println("\n-----------------------------");
            System.out.println("Songs in playlist '" + playlistName + "':");
            for (int i = 0; i < songsInPlaylist.size(); i++) {
                System.out.println(i + ": " + songsInPlaylist.get(i).getFileName());
            }
            System.out.println("-----------------------------");

            // Prompt the user to select a song to delete
            System.out.print("Enter the index of the song to delete: ");
            int songIndex = new Scanner(System.in).nextInt();

            if (songIndex >= 0 && songIndex < songsInPlaylist.size()) {
                String songName = songsInPlaylist.get(songIndex).getFileName().toString();

                // Delete the song from the selected playlist
                playlistService.deleteSongFromPlaylist(playlistName, songName);

                // Display the updated playlist
                displayPlaylistContents(playlistName);
            } else {
                System.out.println("Invalid song index. Please try again.");
            }
        } else {
            System.out.println("Invalid playlist index. Please try again.");
        }
    }

    private void displayPlaylistContents(String playlistName) throws IOException {
        List<String> songs = playlistService.listSongsInPlaylist(playlistName);
        System.out.println("\n-----------------------------");
        System.out.println("Songs in playlist '" + playlistName + "':");
        for (String song : songs) {
            System.out.println("- " + song);
        }
        System.out.println("-----------------------------\n");
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

    private void handleFilterCommand(String input) {
        String[] parts = input.split(" ", 3); // Split into 3 parts: "filter", type, value
        if (parts.length == 3) {
            String filterType = parts[1]; // e.g., "artist"
            String filterValue = parts[2]; // e.g., "Adele"
            mainController.handleFilterCommand(filterType, filterValue);
        } else {
            System.out.println("Invalid Filter command. Use 'filter [artist/song] [value]'.");
        }
    }


    public String getNewDirectory() {
        return newDirectory;
    }
}
