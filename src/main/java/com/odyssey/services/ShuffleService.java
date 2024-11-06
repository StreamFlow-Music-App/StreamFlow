package com.odyssey.services;

import java.util.Collections;
import java.util.List;

public class ShuffleService {

    /**
     * Shuffles the given list of songs.
     *
     * @param songs List of song paths to be shuffled.
     */
    public void shuffleSongs(List<String> songs) {
        Collections.shuffle(songs);
    }
}

