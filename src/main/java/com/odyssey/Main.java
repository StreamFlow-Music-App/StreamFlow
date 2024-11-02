package com.odyssey;

import com.odyssey.controllers.MainController;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String baseDirectory = "src/resources/playlists";
        String initialPlaylist = "songs";
        String currentDirectory = baseDirectory + "/" + initialPlaylist;

        List<String> songs = loadSongsFromFolder(currentDirectory);
        MainController mainController = new MainController(songs);
        mainController.start();

        while (true) {
            System.out.println("Pause -> 'p'");
            System.out.println("Resume -> 'r'");
            System.out.println("Next Song -> 'n'");
            System.out.println("Previous Song -> 'b'");
            System.out.println("Switch Playlist -> 'switch [playlist name]'");
            System.out.println("Stop -> Press Enter");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                mainController.handleInput("stop");
                break;
            } else if (input.startsWith("switch ")) {
                String[] commandParts = input.split(" ", 2);
                if (commandParts.length == 2) {
                    String newPlaylist = commandParts[1];
                    String newDirectory = baseDirectory + "/" + newPlaylist;
                    List<String> newSongs = loadSongsFromFolder(newDirectory);

                    if (!newSongs.isEmpty()) {
                        mainController.setSongs(newSongs);
                        mainController.start();
                    } else {
                        System.out.println("Playlist '" + newPlaylist + "' does not exist or is empty.");
                    }
                } else {
                    System.out.println("Invalid switch command. Use 'switch [playlist name]'.");
                }
            } else {
                mainController.handleInput(input);
            }
        }

        scanner.close();
    }

    private static List<String> loadSongsFromFolder(String directoryPath) {
        List<String> songPaths = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            songPaths = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".mp3"))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading songs directory: " + e.getMessage());
        }

        return songPaths;
    }
}
