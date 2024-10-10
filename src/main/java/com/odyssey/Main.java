package com.odyssey;

import com.odyssey.components.AudioPlayer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AudioPlayer player = new AudioPlayer();
        Scanner scanner = new Scanner(System.in);

        String songPath = "src/resources/songs/Believer(PagalWorld.com.so).mp3";

        System.out.println("Playing song...");
        player.play(songPath);

        System.out.println(player.getCurrentSongPath());

        System.out.println("Press Enter to stop the song...");
        scanner.nextLine();



        player.stop();
        System.out.println("Song stopped.");
        scanner.close();
    }
}
