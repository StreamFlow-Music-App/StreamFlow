package com.odyssey.components;

import com.odyssey.components.utils.FileLoader; // Import the FileLoader class
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FavouriteManager {
    private static final String FAVOURITE_PLAYLIST = "favourites";
    private String baseDirectory;

    public FavouriteManager(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        initializeFavouritePlaylist();
    }

    private void initializeFavouritePlaylist() {
        Path path = Paths.get(baseDirectory, FAVOURITE_PLAYLIST);
        try {
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            System.err.println("Error initializing favourite playlist: " + e.getMessage());
        }
    }

    public void addSongToFavourites(String songPath) throws IOException {
        Path sourcePath = Paths.get(songPath);
        Path destinationPath = Paths.get(baseDirectory, FAVOURITE_PLAYLIST, sourcePath.getFileName().toString());
        Files.copy(sourcePath, destinationPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public void removeSongFromFavourites(String songPath) throws IOException {
        Path path = Paths.get(baseDirectory, FAVOURITE_PLAYLIST, Paths.get(songPath).getFileName().toString());
        Files.delete(path);
    }

    public List<String> getFavouriteSongs() throws IOException {
        return FileLoader.loadSongsFromFolder(Paths.get(baseDirectory, FAVOURITE_PLAYLIST).toString());
    }
}
