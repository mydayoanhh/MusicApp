package com.example.musicapp;

public class Song {
    private String songTitle;
    private String artist;
    private String songLink;
    private String album_art;

    public Song() {
        // Default constructor required for calls to DataSnapshot.getValue(Song.class)
    }

    public Song(String songTitle, String artist, String songLink, String album_art) {
        this.songTitle = songTitle;
        this.artist = artist;
        this.songLink = songLink;
        this.album_art = album_art;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getAlbumArt() {
        return album_art;
    }

    public void setAlbumArt(String album_art) {
        this.album_art = album_art;
    }
}
