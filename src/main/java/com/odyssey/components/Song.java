package com.odyssey.components;

public class Song {
    private String title;
    private String artist;
    private String genre;
    private String album;

    public Song(String title, String artist, String genre, String album) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getAlbum() {
        return album;
    }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + album + ")";
    }
}