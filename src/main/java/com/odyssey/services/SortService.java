package com.odyssey.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortService {

    /**
     * Sorts the songs alphabetically by their names.
     *
     * @param songs List of song paths to be sorted.
     */
    public void sortSongsByName(List<String> songs) {
        Collections.sort(songs, Comparator.comparing(this::getFileNameWithoutExtension));
    }

    /**
     * Utility method to get the file name without its extension.
     *
     * @param filePath Path of the file.
     * @return File name without extension.
     */
    private String getFileNameWithoutExtension(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}


