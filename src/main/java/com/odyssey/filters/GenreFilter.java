package com.odyssey.filters;

import java.util.List;
import java.util.stream.Collectors;

public class GenreFilter {
    public static List<String> filterByGenre(List<String> songs, String genre) {
        return songs.stream()
                .filter(song -> song.toLowerCase().contains(genre.toLowerCase()))
                .collect(Collectors.toList());
    }
}
