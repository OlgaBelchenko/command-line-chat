package org.example.server;

import org.example.logger.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final String SETTINGS_FILE_PATH = "src/main/resources/settings.txt";
    private static final String LOG_FILE_PATH = "src/main/resources/srvlog.txt";
    private static final Map<String, String> settings = getSettingsFromFile();
    private static Logger logger = Logger.getInstance();
    private final ServerSocket serverSocket;
    private Socket socket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer() {
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

    private void shutdownServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
            logger.log("Сервер завершил работу", LOG_FILE_PATH);
        } catch (IOException e) {
            logger.log(e.getMessage(), LOG_FILE_PATH);
            e.printStackTrace();
        }
    }

    private static Map<String, String> getSettingsFromFile() {
        Map<String, String> settings = new HashMap<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE_PATH))) {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 1) {
                    String[] setting = line.split(":", 2);
                    settings.put(setting[0], setting[1]);
                }
            }
        } catch (IOException e) {
            if (logger == null) {
                logger = Logger.getInstance();
            }
            logger.log(e.getMessage(), LOG_FILE_PATH);
            e.printStackTrace();
        }
        return settings;
    }

    private static int getPort() {
        int port = -1;
        for (String setting : settings.keySet()) {
            if ("port".equals(setting)) {
                try {
                    port = Integer.parseInt(settings.get(setting));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return port;
    }

    public static void main(String[] args) throws IOException {
        int port = getPort();
        if (port == -1) {
            throw new IllegalArgumentException("Неверные настройки в файле settings.txt");
        }
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        logger.log("Старт сервера", LOG_FILE_PATH);
        server.runServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.log("Сервер завершил работу", LOG_FILE_PATH);
        }));
    }
}

