package com.odyssey.services;

import java.util.Collections;
import java.util.List;

public class ShuffleService {


    public void shuffleSongs(List<String> songs) {
        Collections.shuffle(songs);
    }
}

