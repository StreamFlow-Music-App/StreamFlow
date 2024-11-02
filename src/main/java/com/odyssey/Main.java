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

        String songsDirectory = "src/resources/songs";

        List<String> songs = loadSongsFromFolder(songsDirectory);

        MainController mainController = new MainController(songs);
        mainController.start();

        while (true) {
            System.out.println("Pause -> 'p'");
            System.out.println("Resume -> 'r'");
            System.out.println("Next Song -> 'n'");
            System.out.println("Previous Song -> 'b'");
            System.out.println("Stop -> Press Enter");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                mainController.handleInput("stop");
                break;
            }

            mainController.handleInput(input);
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
