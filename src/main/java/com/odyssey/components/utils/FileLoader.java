package com.odyssey.components.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLoader {

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
}