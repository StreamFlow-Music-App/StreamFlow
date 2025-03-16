package com.odyssey.components;

import java.util.List;
import java.util.stream.Collectors;

public class SongFilter {

    public List<Song> filterByArtist(List<Song> songs, String artist) {
        return songs.stream()
                .filter(song -> song.getArtist().equalsIgnoreCase(artist)) // Use getArtist()
                .collect(Collectors.toList());
    }

    public List<Song> filterByGenre(List<Song> songs, String genre) {
        return songs.stream()
                .filter(song -> song.getGenre().equalsIgnoreCase(genre)) // Use getGenre()
                .collect(Collectors.toList());
    }

    public List<Song> filterByAlbum(List<Song> songs, String album) {
        return songs.stream()
                .filter(song -> song.getAlbum().equalsIgnoreCase(album)) // Use getAlbum()
                .collect(Collectors.toList());
    }
}