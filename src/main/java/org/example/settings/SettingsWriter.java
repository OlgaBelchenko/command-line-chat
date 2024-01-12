package org.example.settings;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsWriter {
    private static final String SETTINGS_FILE_PATH = "src/main/resources/settings.txt";
    private static final Map<String, String> settings = new HashMap<>();

    public void writeSettingsToFile(String settings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE_PATH, false))) {
            writer.write(settings);
            fillSettingsMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> getSettings() {
        fillSettingsMap();
        return settings;
    }

    public static void fillSettingsMap() {
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE_PATH))) {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 1) {
                    String[] setting = line.split(":", 2);
                    settings.put(setting[0], setting[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
