package com.odyssey.components;

import java.io.*;
import java.util.Properties;

public class StateManager {
    private static final String STATE_FILE = System.getProperty("user.home") + "/state.properties";

    // Save the current state (song path and playback position)
    public void saveState(String currentSongPath, long playbackPosition) {
        if (currentSongPath == null) {
            System.out.println("No song is currently playing. State not saved.");
            return;
        }

        Properties props = new Properties();
        props.setProperty("currentSongPath", currentSongPath);
        props.setProperty("playbackPosition", String.valueOf(playbackPosition));

        try (OutputStream output = new FileOutputStream(STATE_FILE)) {
            props.store(output, "Application State");
            System.out.println("State saved successfully: " + STATE_FILE); // Debug log
        } catch (IOException e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    // Load the saved state
    public Properties loadState() {
        Properties props = new Properties();
        File stateFile = new File(STATE_FILE);

        if (!stateFile.exists()) {
            System.out.println("No saved state found. Starting fresh.");
            return props;
        }

        try (InputStream input = new FileInputStream(STATE_FILE)) {
            props.load(input);
            System.out.println("State loaded successfully: " + STATE_FILE); // Debug log
        } catch (IOException e) {
            System.err.println("Error loading state: " + e.getMessage());
        }
        return props;
    }

    // Reset the state (clear saved data)
    public void resetState() {
        File stateFile = new File(STATE_FILE);
        if (stateFile.exists()) {
            if (stateFile.delete()) {
                System.out.println("State reset successfully.");
            } else {
                System.err.println("Failed to reset state.");
            }
        } else {
            System.out.println("No state to reset.");
        }
    }

}