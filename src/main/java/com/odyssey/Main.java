package com.odyssey;

import com.odyssey.components.AudioPlayer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AudioPlayer player = new AudioPlayer();
        Scanner scanner = new Scanner(System.in);

        String songPath = "src/resources/songs/Shubh - MVP (Official Music Video).mp3";
        System.out.println("Playing song...");
        player.play(songPath);
        System.out.println("Press ENTER to pause/resume the song...");

        // Infinite loop to toggle between pause and play
        while (true) {
            // Wait for the user to press ENTER
            scanner.nextLine();

            // Toggle between play and pause
            if (player.isPaused()) {
                System.out.println("Resuming song...");
                player.play(songPath);  // Resume playback
            } else {
                System.out.println("Pausing song...");
                player.pause();  // Pause playback
            }
        }
    }
}