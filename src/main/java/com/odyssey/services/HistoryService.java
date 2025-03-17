package com.odyssey.services;

import com.odyssey.components.Song;

import java.util.ArrayList;
import java.util.List;

public class HistoryService {
    private final List<String> history;

    public HistoryService() {
        this.history = new ArrayList<>();
    }

    public void addToHistory(String songPath) {
        history.add(songPath);
    }

    public ArrayList<String> getHistory() {
        return new ArrayList<>(history); // Return a copy to prevent external modification
    }

    public void clearHistory() {
        history.clear();
    }
}
