package org.example.server;

import org.example.logger.Logger;
import org.example.settings.SettingsWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 12345;
    private static final String HOST = "127.0.0.1";
    private static final String LOG_FILE_PATH = "src/main/resources/srvlog.txt";
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

    public static void main(String[] args) throws IOException {
        SettingsWriter settings = new SettingsWriter();
        settings.writeSettingsToFile(String.format("port:%d\nhost:%s\n", PORT, HOST));
        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        logger.log("Старт сервера", LOG_FILE_PATH);
        server.runServer();
    }
}

