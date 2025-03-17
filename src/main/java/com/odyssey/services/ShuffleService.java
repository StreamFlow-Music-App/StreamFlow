package com.odyssey.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShuffleService {
    private List<Integer> shuffledIndices;
    private int currentShuffleIndex;

    public void initializeShuffle(int totalSongs) {
        shuffledIndices.clear();
        for (int i = 0; i < totalSongs; i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices); // Shuffle the indices
        currentShuffleIndex = 0; // Start from the first shuffled index
    }

    public int getNextShuffledIndex() {
        if (currentShuffleIndex < shuffledIndices.size() - 1) {
            currentShuffleIndex++;
        } else {
            currentShuffleIndex = 0; // Loop back to the first index
        }
        return shuffledIndices.get(currentShuffleIndex);
    }

    public int getPreviousShuffledIndex() {
        if (currentShuffleIndex > 0) {
            currentShuffleIndex--;
        } else {
            currentShuffleIndex = shuffledIndices.size() - 1; // Loop back to the last index
        }
        return shuffledIndices.get(currentShuffleIndex);
    }
}
