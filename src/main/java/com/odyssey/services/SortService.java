package com.odyssey.services;

import com.odyssey.components.Song;
import java.util.Comparator;
import java.util.List;

public class SortService {

    // Sort songs by title
    public List<Song> sortByTitle(List<Song> songs) {
        songs.sort(Comparator.comparing(Song::getTitle));
        return songs;
    }

    // Sort songs by artist
    public List<Song> sortByArtist(List<Song> songs) {
        songs.sort(Comparator.comparing(Song::getArtist));
        return songs;
    }

    // Sort songs by album
    public List<Song> sortByAlbum(List<Song> songs) {
        songs.sort(Comparator.comparing(Song::getAlbum));
        return songs;
    }
}