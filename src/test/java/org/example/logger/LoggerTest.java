package org.example.logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {
    String filePath = "src/main/resources/test_res/test_logger.txt";

    @Test
    void test_log() {
        Logger logger = Logger.getInstance();
        String msg = "Test Message";
        logger.log(msg, filePath);
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
        assertTrue(result);
    }

    @AfterEach
    void emptyTestFile() {
        try (PrintWriter writer = new PrintWriter(filePath)){
            writer.print("");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}