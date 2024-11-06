package com.odyssey.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SearchService {

    /**
     * Searches for songs by name in a list of available song paths.
     *
     * @param songs      List of available song paths as strings.
     * @param songName   Name of the song to search for.
     * @return           List of matching songs paths.
     */
    public List<String> searchSongsByName(List<String> songs, String songName) {
        return songs.stream()
                .filter(path -> getFileNameWithoutExtension(path).equalsIgnoreCase(songName))
                .collect(Collectors.toList());
    }

    /**
     * Utility method to get file name without extension.
     *
     * @param filePath   Path to the file.
     * @return           File name without extension.
     */
    private String getFileNameWithoutExtension(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}
