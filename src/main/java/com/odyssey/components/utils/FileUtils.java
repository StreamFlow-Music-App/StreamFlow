package com.odyssey.components.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static List<String> loadSongsFromFolder(String directoryPath) {
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

    public static void createNewPlaylist(String directoryPath) {
        Path path = Paths.get(directoryPath);
        try {
            if (Files.notExists(path)) {
                Files.createDirectory(path);
                System.out.println("Playlist '" + path.getFileName() + "' created successfully.");
            } else {
                System.out.println("Playlist '" + path.getFileName() + "' already exists.");
            }
        } catch (IOException e) {
            System.err.println("Error creating playlist: " + e.getMessage());
        }
    }

    public static void deletePlaylist(String directoryPath) {
        Path path = Paths.get(directoryPath);
        try {
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                System.out.println("Playlist '" + path.getFileName() + "' deleted successfully.");
            } else {
                System.out.println("Playlist '" + path.getFileName() + "' does not exist.");
            }
        } catch (IOException e) {
            System.err.println("Error deleting playlist: " + e.getMessage());
        }
    }
}

