package org.example.settings;

import java.util.Map;

public class SettingsReader {

    public int getPort() {
        Map<String, String> settings = SettingsWriter.getSettings();
        return Integer.parseInt(settings.getOrDefault("port", "-1"));
    }

    public String getHost() {
        Map<String, String> settings = SettingsWriter.getSettings();
        return settings.getOrDefault("host", "");
    }
}
