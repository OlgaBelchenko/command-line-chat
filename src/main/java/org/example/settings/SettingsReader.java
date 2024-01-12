package org.example.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsReader {

    private final String SETTINGS_FILE_PATH = "src/main/resources/settings.txt";
    private final Map<String, String> settings = new HashMap<>();

    public SettingsReader() {
        fillSettingsMap();
    }

    public int getPort() {
        return Integer.parseInt(settings.getOrDefault("port", "-1"));
    }

    public String getHost() {
        return settings.getOrDefault("host", "");
    }

    private void fillSettingsMap() {
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
