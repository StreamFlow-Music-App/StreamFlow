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

    public static Song fromFilePath(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1); // Extract file name
        String title = fileName.replace(".mp3", ""); // Remove file extension
        String artist = "Unknown Artist"; // Default artist
        if (fileName.contains(" - ")) {
            String[] parts = fileName.split(" - ");
            title = parts[0].replace(".mp3", "");
            artist = parts.length > 1 ? parts[1].replace(".mp3", "") : "Unknown Artist";
        }
        return new Song(title, artist, "Unknown Genre", "Unknown Album");
    }

}
