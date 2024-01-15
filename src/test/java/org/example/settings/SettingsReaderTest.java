package org.example.settings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SettingsReaderTest {

    static String filePath = "src/main/resources/test_res/test_settings_reader.txt";
    static String emptyFilePath = "src/main/resources/test_res/test_settings_reader_empty_file.txt";
    static String PORT = "1111";
    static String HOST = "locahost";
    SettingsReader settingsReader = new SettingsReader(filePath);
    SettingsReader settingsReaderEmptyFile = new SettingsReader(emptyFilePath);

    @BeforeAll
    static void writeSettingsToFile() {
        SettingsWriter settingsWriter = new SettingsWriter();
        String msg = String.format("port:%d\nhost:%s\n", Integer.parseInt(PORT), HOST);
        settingsWriter.writeSettingsToFile(msg, filePath);
    }

    @Test
    void test_getSetting_returns_correct_port() {
        assertEquals(PORT, settingsReader.getSetting("port"));
    }

    @Test
    void test_getSetting_returns_correct_host() {
        assertEquals(HOST, settingsReader.getSetting("host"));
    }

    @Test
    void test_getSetting_correct_value_when_empty_file() {
        assertEquals("", settingsReaderEmptyFile.getSetting("port"));
    }
}