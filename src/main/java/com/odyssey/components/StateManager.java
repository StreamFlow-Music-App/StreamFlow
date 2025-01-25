package com.odyssey.components;

import com.odyssey.filters.ArtistFilter;
import com.odyssey.filters.GenreFilter;


import java.util.ArrayList;
import java.util.List;

public class StateManager {
    private long currentTime;
    private double playbackSpeed;
    private List<String> songs;
    private String currentSong;

    public StateManager(List<String> songs) {
        this.currentTime = 0;
        this.playbackSpeed = 1.0;
        this.songs = songs;
    }

    public void updatePlaybackTime(long timeElapsed) {
        this.currentTime += timeElapsed;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void resetPlaybackTime() {
        this.currentTime = 0;
    }

    public double getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed(double speed) {
        if (speed > 0.5 && speed <= 2.0) {
            this.playbackSpeed = speed;
            System.out.println("Playback speed set to " + playbackSpeed + "x");
        } else {
            System.out.println("Invalid speed. Please set a value between 0.5x and 2.0x.");
        }
    }



    public List<String> filterSongsByArtist(String artist) {
        return ArtistFilter.filterByArtist(songs, artist);
    }

    public List<String> filterSongsByGenre(String genre) {
        return GenreFilter.filterByGenre(songs, genre);
    }

    public void setCurrentSong(String song) {
        this.currentSong = song;
    }

    public String getCurrentSong() {
        return currentSong;
    }


}
