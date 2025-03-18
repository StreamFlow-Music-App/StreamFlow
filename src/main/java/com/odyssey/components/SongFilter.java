package com.odyssey.components;

import java.util.List;
import java.util.stream.Collectors;

public class SongFilter {

    public List<String> filterByArtist(List<String> songPaths, String artist) {
        return songPaths.stream()
                .filter(songPath -> {
                    String extractedArtist = extractArtistFromFileName(songPath);
                    return extractedArtist.toLowerCase().contains(artist.toLowerCase()); // Partial match
                })
                .collect(Collectors.toList());
    }

    public List<String> filterByGenre(List<String> songPaths, String genre) {
        return songPaths.stream()
                .filter(songPath -> extractGenreFromPath(songPath).equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    public List<String> filterByAlbum(List<String> songPaths, String album) {
        return songPaths.stream()
                .filter(songPath -> extractAlbumFromPath(songPath).equalsIgnoreCase(album))
                .collect(Collectors.toList());
    }

    public List<String> filterBySongName(List<String> songPaths, String songName) {
        return songPaths.stream()
                .filter(songPath -> {
                    String extractedSongName = extractSongNameFromFileName(songPath);
                    return extractedSongName.toLowerCase().contains(songName.toLowerCase()); // Partial match
                })
                .collect(Collectors.toList());
    }

    private String extractArtistFromPath(String songPath) {
        // Example path: "src/resources/songs/ArtistName/AlbumName/SongName.mp3"
        String[] parts = songPath.split("/");
        if (parts.length > 3) {
            return parts[3]; // Artist name is the 4th part of the path
        }
        return "Unknown Artist";
    }

    private String extractGenreFromPath(String songPath) {
        return "Unknown Genre";
    }

    private String extractArtistFromFileName(String songPath) {
        String fileName = songPath.substring(songPath.lastIndexOf("/") + 1); // Get the file name
        if (fileName.contains(" - ")) {
            return fileName.split(" - ")[1].replace(".mp3", ""); // Extract artist name
        }
        return "Unknown Artist";
    }

    private String extractAlbumFromPath(String songPath) {
        // Example path: "src/resources/songs/ArtistName/AlbumName/SongName.mp3"
        String[] parts = songPath.split("/");
        if (parts.length > 4) {
            return parts[4]; // Album name is the 5th part of the path
        }
        return "Unknown Album";
    }

    private String extractSongNameFromFileName(String songPath) {
        String fileName = songPath.substring(songPath.lastIndexOf("/") + 1); // Get the file name
        if (fileName.contains(" - ")) {
            return fileName.split(" - ")[0]; // Extract song name
        }
        return fileName.replace(".mp3", ""); // Fallback to file name without extension
    }
}