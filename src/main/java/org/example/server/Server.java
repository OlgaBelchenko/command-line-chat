package org.example.server;

import org.example.logger.Logger;
import org.example.settings.SettingsWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Server {
    private static final String PORT = "12345";
    private static final String HOST = "127.0.0.1";
    private static final String LOG_FILE_PATH = "src/main/resources/srv_log.txt";
    private static final String SETTINGS_FILE_PATH = "src/main/resources/settings.txt";
    private static final Logger logger = Logger.getInstance();
    private final ServerSocket serverSocket;
    private Socket socket;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(Integer.parseInt(PORT));
            writeSettingsToFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeSettingsToFile() {
        SettingsWriter settings = new SettingsWriter(SETTINGS_FILE_PATH);
        settings.writeSetting("port", PORT);
        settings.writeSetting("host", HOST);
    }

    public void runServer() {
        logger.log("Старт сервера", LOG_FILE_PATH);
        new Thread(this::waitForExitCommand).start();
        try {
            while (!serverSocket.isClosed()) {
                socket = serverSocket.accept();
                logger.log("Подключился новый пользователь", LOG_FILE_PATH);
                ClientManager clientManager = new ClientManager(socket);
                Thread thread = new Thread(clientManager);
                thread.start();
            }
        } catch (IOException e) {
            logger.log(e.getMessage(), LOG_FILE_PATH);
            shutdownServer();
        }
    }

    private void waitForExitCommand() {
        Scanner scanner = new Scanner(System.in);
        final String SHUTDOWN_COMMAND = "shutdown";
        String command;
        while (true) {
            System.out.println("Для завершения работы сервера введите shutdown");
            command = scanner.nextLine();
            if (SHUTDOWN_COMMAND.equals(command)) {
                shutdownServer();
                break;
            }
        }
    }

    private void shutdownServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
            logger.log("Сервер завершил работу", LOG_FILE_PATH);
            exit(0);
        } catch (IOException e) {
            logger.log(e.getMessage(), LOG_FILE_PATH);
            e.printStackTrace();
        }
    }
}

