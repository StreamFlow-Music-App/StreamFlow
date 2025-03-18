package com.odyssey.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchService {

    /**
     * Searches for songs by name and returns indices of matching songs.
     *
     * @param songs    List of available song paths as strings.
     * @param songName Name of the song to search for.
     * @return List of indices of matching songs.
     */
    public List<Integer> searchSongsByName(List<String> songs, String songName) {
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            String fileName = getFileNameWithoutExtension(songs.get(i));
            if (fileName.equalsIgnoreCase(songName)) {
                matchingIndices.add(i);
            }
        }
        return matchingIndices;
    }

    /**
     * Prompts the user to select a song index to play from the list of matches.
     *
     * @param matchingIndices List of indices of matching songs.
     * @param songs           List of all song paths.
     * @return Index of the selected song to play, or -1 if invalid.
     */
    public int promptForSongIndex(List<Integer> matchingIndices, List<String> songs) {
        System.out.println("Matching songs:");
        for (int index : matchingIndices) {
            System.out.println(index + ": " + getFileNameWithoutExtension(songs.get(index)));
        }

        System.out.print("Enter the index of the song you want to play: ");
        Scanner scanner = new Scanner(System.in);
        int selectedIndex = scanner.nextInt();

        return matchingIndices.contains(selectedIndex) ? selectedIndex : -1;
    }

    private String getFileNameWithoutExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? filePath : filePath.substring(0, dotIndex);
    }
}
