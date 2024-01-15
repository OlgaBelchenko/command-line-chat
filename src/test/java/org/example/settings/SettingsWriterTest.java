package org.example.settings;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SettingsWriterTest {

    @Test
    void test_writeSettingsToFile() {
        SettingsWriter writer = new SettingsWriter();
        String filePath = "src/main/resources/test_res/test_settings_writer.txt";
        String msg = "Test Message";
        writer.writeSettingsToFile(msg, filePath);
        boolean result = false;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(msg)) {
                    result = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            fail();
        }
        assert (result);
    }
}