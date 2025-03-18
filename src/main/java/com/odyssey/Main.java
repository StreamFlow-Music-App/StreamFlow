package com.odyssey;

import com.odyssey.components.CommandHandler;
import com.odyssey.components.PlaylistManager;
import com.odyssey.components.utils.FileLoader;
import com.odyssey.controllers.MainController;
import com.odyssey.services.HistoryService;
import com.odyssey.services.PlaylistService;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String YELLOW = "\u001B[33m";
        String GREEN = "\u001B[32m";
        String BLUE = "\u001B[34m";

        System.out.println(CYAN + "╔════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║" + RESET + BOLD + "     Welcome to StreamFlow Harmony Player!  " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "╠════════════════════════════════════════════╣" + RESET);
        System.out.println(CYAN + "║" + RESET + GREEN + "  1. " + RESET + BOLD + "Login                                 " + RESET + CYAN + " ║" + RESET);
        System.out.println(CYAN + "║" + RESET + YELLOW + "  2. " + RESET + BOLD + "Register                              " + RESET + CYAN + " ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════╝" + RESET);
        System.out.print(BLUE + "Choose an option (1/2): " + RESET);

        int choice = scanner.nextInt();
        scanner.nextLine();

        boolean authenticated = false;
        boolean isPremium = false;
        String username = null;

        if (choice == 1) {
            username = authenticateUser(scanner);
            authenticated = username != null;
            if (authenticated) {
                isPremium = checkPremiumStatus(username);
            }
        } else if (choice == 2) {
            registerUser(scanner);
            System.out.println("Registration successful! Please log in.");
            username = authenticateUser(scanner);
            authenticated = username != null;
        } else {
            System.out.println("Invalid choice. Exiting...");
            return;
        }

        if (!authenticated) {
            System.out.println("Invalid credentials. Exiting application.");
            return;
        }

        try {
            String baseDirectory = "src/resources/playlists";
            String initialPlaylist = "songs";
            String currentDirectory = baseDirectory + "/" + initialPlaylist;

            List<String> songs = FileLoader.loadSongsFromFolder(currentDirectory);
            HistoryService historyService = new HistoryService();
            MainController mainController = new MainController(songs, historyService);
            PlaylistService playlistService = new PlaylistService();
            mainController.start();

            CommandHandler commandHandler = new CommandHandler(baseDirectory, mainController, playlistService);
            PlaylistManager playlistManager = new PlaylistManager(baseDirectory);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (mainController.getCurrentSongPath() != null) {
                    mainController.saveState();
                    System.out.println("State saved successfully.");
                } else {
                    System.out.println("No song is playing. State not saved.");
                }
            }));

            while (true) {
                String directory = commandHandler.getNewDirectory();
                displayCommands(directory, isPremium);

                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("m") && !isPremium) {
                    if (upgradeToPremium(username, scanner)) {
                        isPremium = true;
                    }
                    continue;
                }

                if (input.equalsIgnoreCase("reset")) {
                    mainController.resetState();
                } else {
                    commandHandler.handleCommand(input, playlistManager, currentDirectory);
                }

                if (input.isEmpty()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void displayCommands(String directory, boolean isPremium) {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String YELLOW = "\u001B[33m";
        String GREEN = "\u001B[32m";
        String BLUE = "\u001B[34m";
        String RED = "\u001B[31m";
        String PURPLE = "\u001B[35m";
        String WHITE = "\u001B[37m";

        System.out.println(CYAN + "══════════════════════════════════════════" + RESET);
        System.out.println(CYAN + "      Available Commands" + RESET);
        System.out.println(CYAN + "══════════════════════════════════════════" + RESET);

        System.out.println(WHITE + BOLD + "General Commands:" + RESET);
        System.out.println(GREEN + "  - Next Song -> 'n'" + RESET);
        System.out.println(GREEN + "  - Previous Song -> 'b'" + RESET);
        System.out.println(GREEN + "  - Pause -> 'p'" + RESET);
        System.out.println(GREEN + "  - Resume -> 'r'" + RESET);
        System.out.println(GREEN + "  - Search Song -> 's'" + RESET);
        System.out.println(GREEN + "  - Shuffle Play -> 'c'" + RESET);
        System.out.println(GREEN + "  - Add to Favourites -> 'f'" + RESET);
        System.out.println(GREEN + "  - List Favourites -> 'l'" + RESET);
        System.out.println(GREEN + "  - Show Playback Time -> 't'" + RESET);
        System.out.println(GREEN + "  - Set Playback Speed -> 'speed'" + RESET);
        System.out.println(GREEN + "  - Show History -> 'h'" + RESET);
        System.out.println(GREEN + "  - Filter Songs -> 'filter [artist/song] [value]'" + RESET);
        System.out.println(GREEN + "  - Sort Songs -> 'sort [title/artist/album]'" + RESET);

        if (isPremium) {
            System.out.println(PURPLE + "\nPremium Features:" + RESET);
            if (!Objects.equals(directory, null) && !Objects.equals(directory, "src/resources/playlists/songs")) {
                System.out.println(YELLOW + "  - Add Song -> 'add song'" + RESET);
                System.out.println(YELLOW + "  - Remove Song -> 'remove song'" + RESET);
            }
            System.out.println(YELLOW + "  - Create Playlist -> 'create [playlist name]'" + RESET);
            System.out.println(YELLOW + "  - Delete Playlist -> 'delete [playlist name]'" + RESET);
            System.out.println(YELLOW + "  - Switch Playlist -> 'switch [playlist name]'" + RESET);
        } else {
            System.out.println(RED + "\n------------------------------------------" + RESET);
            System.out.println(RED + "  Get Membership for only $1 -> 'm'" + RESET);
            System.out.println(RED + "------------------------------------------" + RESET);
        }

        System.out.println("\n" + WHITE + BOLD + "Other Commands:" + RESET);
        System.out.println(GREEN + "  - Reset State -> 'reset'" + RESET);
        System.out.println(GREEN + "  - Stop -> Press Enter" + RESET);

        System.out.println(CYAN + "══════════════════════════════════════════" + RESET + "\n");
    }


    private static String authenticateUser(Scanner scanner) {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String YELLOW = "\u001B[33m";
        String GREEN = "\u001B[32m";
        String BLUE = "\u001B[34m";
        String RED = "\u001B[31m";

        System.out.println(CYAN + "════════════════════════════════════" + RESET);
        System.out.println(CYAN + "          Login Information         " + RESET);
        System.out.println(CYAN + "════════════════════════════════════" + RESET);

        System.out.print(BOLD + BLUE + "  Enter username: " + RESET);
        String username = scanner.nextLine();

        System.out.print(BOLD + BLUE + "  Enter password: " + RESET);
        String password = scanner.nextLine();

        System.out.println(CYAN + "════════════════════════════════════" + RESET);


        if (authenticate(username, password)) {
            return username;
        }
        return null;
    }

    private static boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/login.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username) && credentials[1].trim().equals(password)) {
                    System.out.println("Login successful! Welcome, " + username);
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }
        return false;
    }

    private static boolean checkPremiumStatus(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/login.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username) && credentials[2].trim().equals("true")) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }
        return false;
    }

    private static boolean upgradeToPremium(String username, Scanner scanner) {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String BLUE = "\u001B[34m";


        System.out.println(CYAN + "════════════════════════════════════" + RESET);
        System.out.println(CYAN + "   Enter Credit Card Information" + RESET);
        System.out.println(CYAN + "════════════════════════════════════" + RESET);

        System.out.print(BOLD + BLUE + "  Enter Credit Card Number [16 digits]: " + RESET);
        String cardNumber = scanner.nextLine();

        System.out.print(BOLD + BLUE + "  Enter Expiry Date (MM/YY): " + RESET);
        String expiryDate = scanner.nextLine();

        System.out.print(BOLD + BLUE + "  Enter CVC: " + RESET);
        String cvc = scanner.nextLine();

        System.out.println(CYAN + "════════════════════════════════════" + RESET);


        if (!cardNumber.matches("\\d{16}") || !expiryDate.matches("\\d{2}/\\d{2}") || !cvc.matches("\\d{3}")) {
            System.out.println("Invalid payment details. Try again.");
            return false;
        }

        try {
            File file = new File("src/resources/login.txt");
            File tempFile = new File("src/resources/temp_login.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username)) {
                    writer.write(credentials[0] + "," + credentials[1] + ",true\n");
                } else {
                    writer.write(line + "\n");
                }
            }
            reader.close();
            writer.close();

            file.delete();
            tempFile.renameTo(file);

            System.out.println("Membership activated successfully!");
            return true;
        } catch (IOException e) {
            System.err.println("Error updating membership: " + e.getMessage());
        }
        return false;
    }

    private static void registerUser(Scanner scanner) {
        String RESET = "\u001B[0m";
        String BOLD = "\u001B[1m";
        String CYAN = "\u001B[36m";
        String GREEN = "\u001B[32m";
        String BLUE = "\u001B[34m";
        String RED = "\u001B[31m";

        System.out.println(CYAN + "════════════════════════════════════" + RESET);
        System.out.println(CYAN + "          Register New Account      " + RESET);
        System.out.println(CYAN + "════════════════════════════════════" + RESET);

        System.out.print(BOLD + BLUE + "Choose a username: " + RESET);
        String username = scanner.nextLine();

        System.out.print(BOLD + BLUE + "Choose a password: " + RESET);
        String password = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/resources/login.txt", true))) {
            writer.write(username + "," + password + ",false\n");
            System.out.println(GREEN + "Account created successfully." + RESET);
        } catch (IOException e) {
            System.err.println(RED + "Error writing to login file: " + e.getMessage() + RESET);
        }

        System.out.println(CYAN + "════════════════════════════════════" + RESET);

    }
}