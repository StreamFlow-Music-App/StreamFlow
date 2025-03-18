package com.odyssey.services;

import java.util.ArrayList;
import java.util.List;

public class HistoryService {
    private final List<String> history;

    public HistoryService() {
        this.history = new ArrayList<>();
    }

    public ArrayList<String> addToHistory(String songPath) {

        if (songPath != null) {
            history.add(songPath); // Add the song path to the history list
        }
        return null;
    }

    public ArrayList<String> getHistory() {
        return new ArrayList<>(history); // Return a copy to prevent external modification
    }

    public void clearHistory() {
        history.clear();
    }
}
