package com.odyssey.components;

import java.util.ArrayList;
import java.util.List;

public class FavouriteManager {
    private List<String> favouriteSongs;

    public FavouriteManager() {
        favouriteSongs = new ArrayList<>();
    }

    public void addFavourite(String song) {
        if (!favouriteSongs.contains(song)) {
            favouriteSongs.add(song);
            System.out.println("Added to favorites: " + song);
        } else {
            System.out.println("Song is already in favorites.");
        }
    }

    public List<String> getFavourites() {
        return favouriteSongs;
    }
}
