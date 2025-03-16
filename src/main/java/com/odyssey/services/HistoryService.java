package com.odyssey.services;

import com.odyssey.components.Song;

import java.util.ArrayList;
import java.util.List;

public class HistoryService {
    private final List<Song> history;

    public HistoryService() {
        this.history = new ArrayList<>();
    }

    public void addToHistory(Song song) {
        history.add(song);
    }

    public List<Song> getHistory() {
        return new ArrayList<>(history); // Return a copy to prevent external modification
    }

    public void clearHistory() {
        history.clear();
    }
}
