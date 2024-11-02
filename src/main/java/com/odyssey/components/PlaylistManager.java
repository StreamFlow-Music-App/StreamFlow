package com.odyssey.components;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class PlaylistManager {
    private String baseDirectory;

    public PlaylistManager(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public void createNewPlaylist(String playlistName) {
        Path path = Paths.get(baseDirectory, playlistName);
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

    public void deletePlaylist(String playlistName) {
        Path path = Paths.get(baseDirectory, playlistName);
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
