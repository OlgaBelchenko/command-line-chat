package org.example.settings;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsWriter {


    private final Map<String, String> settings = new HashMap<>();
    private final String settingsFilePath;

    public SettingsWriter(String settingsFilePath) {
        this.settingsFilePath = settingsFilePath;
    }

    public void writeSetting(String settingName, String settingValue) {
        settings.put(settingName, settingValue);
        writeSettingsToFile();
    }

    private void writeSettingsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(settingsFilePath, false))) {
            for (Map.Entry<String, String> setting : settings.entrySet()) {
                String settingToWrite = String.format("%s:%s\n", setting.getKey(), setting.getValue());
                writer.write(settingToWrite);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
