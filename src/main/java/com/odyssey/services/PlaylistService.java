package com.odyssey.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public void addSongToPlaylist(String playlistName, String songPath) throws IOException {
        Path sourcePath = Paths.get(songPath);
        String songName = sourcePath.getFileName().toString();

        // Check if the song already exists in the playlist
        if (doesSongExistInPlaylist(playlistName, songName)) {
            System.out.println("This song already exists in the playlist: " + playlistName);
            return;
        }

        // Copy the song to the playlist folder
        Path destinationPath = Paths.get("src/resources/playlists/" + playlistName, songName);
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Song added to playlist: " + playlistName);
    }

    public void deleteSongFromPlaylist(String playlistName, String songName) throws IOException {
        Path songPath = Paths.get("src/resources/playlists/" + playlistName, songName);

        // Delete the song from the playlist folder
        Files.delete(songPath);
        System.out.println("Song deleted from playlist: " + playlistName);
    }

    public boolean doesSongExistInPlaylist(String playlistName, String songName) throws IOException {
        List<String> songs = listSongsInPlaylist(playlistName);
        return songs.contains(songName);
    }

    public List<String> listSongsInPlaylist(String playlistName) throws IOException {
        return Files.list(Paths.get("src/resources/playlists/" + playlistName))
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }
}




