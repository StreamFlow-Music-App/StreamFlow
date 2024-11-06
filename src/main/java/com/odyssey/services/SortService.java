package com.odyssey.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortService {


    public void sortSongsByName(List<String> songs) {
        Collections.sort(songs, Comparator.comparing(this::getFileNameWithoutExtension));
    }


    private String getFileNameWithoutExtension(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}


