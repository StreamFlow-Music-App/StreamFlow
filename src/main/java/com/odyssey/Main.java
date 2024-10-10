package com.odyssey;

import com.odyssey.controllers.MainController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        List<String> songs = new ArrayList<>();
        songs.add("src/resources/songs/11086_Luis Fonsi ft. Daddy Yankee - Despacito SHOW2BABI.COM.mp3");
        songs.add("src/resources/songs/Believer(PagalWorld.com.so).mp3");
        songs.add("src/resources/songs/Imagine-Dragons-Bad-Liar-(RawPraise.ng).mp3");

        MainController mainController = new MainController(songs);
        mainController.start();

        while (true) {
            System.out.println("Press 'p' to pause, 'r' to resume, 'n' for next, 'b' for previous, or 'Enter' to stop...");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                mainController.handleInput("stop");
                break;
            }

            mainController.handleInput(input);
        }

        scanner.close();
    }
}
