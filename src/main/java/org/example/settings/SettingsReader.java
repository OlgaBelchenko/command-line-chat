package org.example.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsReader {

    private final String settingsFilePath;
    private final Map<String, String> settings = new HashMap<>();

    public SettingsReader(String settingsFilePath) {
        this.settingsFilePath = settingsFilePath;
        fillSettingsMap();
    }

    public String getSetting(String setting) {
        return settings.getOrDefault(setting, "");
    }

    private void fillSettingsMap() {
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFilePath))) {
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
