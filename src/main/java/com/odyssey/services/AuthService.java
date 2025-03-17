package com.odyssey.services;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class AuthService {
    private boolean membership;
    private String username;

    private static final String LOGIN_FILE = "src/main/resources/login.txt";

    public boolean authenticateUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        boolean isAuthenticated = authenticate(username, password);
        if (isAuthenticated) {
            System.out.println("Authentication successful!");
        }
        return isAuthenticated;
    }

    public boolean getMembership() {
        return membership;
    }

    public String getUsername(){
        return username;
    }

    private boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username) && credentials[1].trim().equals(password)) {
                    this.username = username;
                    membership = Objects.equals(credentials[2].trim(), "true");
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }
        return false;
    }

    public void registerUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter a new password: ");
        String password = scanner.nextLine();

        if (isUserExists(username)) {
            System.out.println("Username already exists. Try a different one.");
            return;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOGIN_FILE, true))) {
            bufferedWriter.write(username + "," + password + ",false");
            bufferedWriter.newLine();
            System.out.println("Registration successful! You can now log in.");
        } catch (IOException e) {
            System.err.println("Error writing to login file: " + e.getMessage());
        }
    }

    private boolean isUserExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].trim().equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading login file: " + e.getMessage());
        }
        return false;
    }
}
