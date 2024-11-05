package com.odyssey.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

public class PlaylistService {

    public void addSong(String destination, String source) throws IOException {
        Path destinationPath = Paths.get(destination);
        Path sourcePath = Paths.get(source);
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void removeSong(String songPath) throws IOException {
        Path path = Paths.get(songPath);
        Files.delete(path);
    }

    public Optional<String> searchSongByName(String directoryPath, String songName) {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().contains(songName) && p.toString().endsWith(".mp3"))
                    .map(Path::toString)
                    .findFirst();
        } catch (IOException e) {
            System.err.println("Error searching for song: " + e.getMessage());
        }
        return Optional.empty();
    }
}
