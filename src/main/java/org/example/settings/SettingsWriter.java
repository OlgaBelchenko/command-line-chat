package org.example.settings;

import java.io.*;

public class SettingsWriter {
    private final String SETTINGS_FILE_PATH = "src/main/resources/settings.txt";

    public void writeSettingsToFile(String settings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE_PATH, false))) {
            writer.write(settings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
