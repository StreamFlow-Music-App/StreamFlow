package com.odyssey.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
}
