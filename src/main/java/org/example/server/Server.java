package org.example.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

public class Server {

    private final ServerSocket serverSocket;
    private Socket socket;
    // TODO: add logger

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer() {
        try {
            while (!serverSocket.isClosed()) {
                socket = serverSocket.accept();
                // TODO: log
                System.out.println("Подключился новый пользователь!");
                ClientManager clientManager = new ClientManager(socket);
                Thread thread = new Thread(clientManager);
                thread.start();
            }
        } catch (IOException e) {
            shutdownServer();
        }
    }

    private Socket getSocketFromFile() {
        // TODO
        return null;
    }

    private void shutdownServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // TODO: change later to read port number from settings file
        int port = getPort("src/main/java/org/example/server/settings.txt");
        if (port == -1) {
            throw new IllegalArgumentException("Неверные настройки в файле");
        }
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.runServer();
    }

    private static Map<String, String> getSettingsFromFile(String fileName) {
        Map<String, String> settings = new HashMap<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 1) {
                    String[] setting = line.split(":", 2);
                    settings.put(setting[0], setting[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    private static int getPort(String fileName) {
        Map<String, String> settings = getSettingsFromFile(fileName);
        int port = -1;
        for (String setting : settings.keySet()) {
            if ("port".equals(setting)) {
                try {
                    port = Integer.parseInt(settings.get(setting));
                } catch (NumberFormatException e) {
                    System.out.println("Неверные настройки в файле settings.txt");
                }
            }
        }
        return port;
    }
}
