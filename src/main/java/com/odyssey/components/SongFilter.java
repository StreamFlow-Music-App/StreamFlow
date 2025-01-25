package com.odyssey.components;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SongFilter {
    private List<Song> songs;
    private List<Song> customPlaylist;

    public SongFilter(String songFilePath) throws IOException {
        this.songs = loadSongsFromFile(songFilePath);
        this.customPlaylist = new ArrayList<>();
    }

    private List<Song> loadSongsFromFile(String filePath) throws IOException {
        List<Song> songs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("-");
                if (parts.length == 3) {
                    songs.add(new Song(parts[0], parts[1], parts[2]));
                }
            }
        }
        return songs;
    }

    public List<Song> filterByArtist(String artistName) {
        return songs.stream()
                .filter(song -> song.getArtist().equalsIgnoreCase(artistName))
                .collect(Collectors.toList());
    }

    public List<Song> filterByGenre(String genre) {
        return songs.stream()
                .filter(song -> song.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    public void addToCustomPlaylist(String keyword) {
        List<Song> matchingSongs = songs.stream()
                .filter(song -> song.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        customPlaylist.addAll(matchingSongs);
        System.out.println(matchingSongs.size() + " songs added to the custom playlist using keyword: " + keyword);
    }

    public void saveCustomPlaylist(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Song song : customPlaylist) {
                writer.write(song.getTitle() + "-" + song.getArtist() + "-" + song.getGenre());
                writer.newLine();
            }
        }
        System.out.println("Custom playlist saved to " + filePath);
    }
}



