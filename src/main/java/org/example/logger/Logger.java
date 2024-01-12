package org.example.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Logger {
    private static Logger instance = null;

    private Logger(){}

    public void log(String msg, String filePath) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String dateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(timestamp);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.write(String.format("[%s] %s\n", dateTime, msg));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance;
    }
}
