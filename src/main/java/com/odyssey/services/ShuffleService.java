package com.odyssey.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShuffleService {
    private List<Integer> shuffledIndices;
    private int currentShuffleIndex;

    public void initializeShuffle(int songCount) {
        shuffledIndices = IntStream.range(0, songCount).boxed().collect(Collectors.toList());
        Collections.shuffle(shuffledIndices);
        currentShuffleIndex = 0;
    }

    public int getNextShuffledIndex() {
        if (shuffledIndices == null || shuffledIndices.isEmpty()) {
            throw new IllegalStateException("Shuffle not initialized.");
        }

        int index = shuffledIndices.get(currentShuffleIndex);
        currentShuffleIndex = (currentShuffleIndex + 1) % shuffledIndices.size();
        return index;
    }


}
