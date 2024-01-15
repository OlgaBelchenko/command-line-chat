package org.example.settings;

import java.io.*;

public class SettingsWriter {

    public void writeSettingsToFile(String settings, String settingsFilePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(settingsFilePath, false))) {
            writer.write(settings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
