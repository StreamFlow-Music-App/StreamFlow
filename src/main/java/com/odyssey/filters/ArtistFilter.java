package com.odyssey.filters;

import java.util.List;
import java.util.stream.Collectors;

public class ArtistFilter {
    public static List<String> filterByArtist(List<String> songs, String artist) {
        return songs.stream()
                .filter(song -> song.toLowerCase().contains(artist.toLowerCase()))
                .collect(Collectors.toList());
    }
}
