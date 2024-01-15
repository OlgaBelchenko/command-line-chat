package org.example.server;

import org.example.logger.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientManager implements Runnable {
    private static final List<ClientManager> clients = new ArrayList<>();
    private static final Logger logger = Logger.getInstance();
    private final String EXIT_COMMAND = "/exit";
    private final String LOG_FILE_PATH = "src/main/resources/srv_log.txt";

    private final Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String userName;


    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = in.readLine();
            out.write("Добро пожаловать в чат, " + userName + "!");
            out.newLine();
            out.flush();
            String messageToBroadcast = "Пользователь " + userName + " вошел в чат!";
            logger.log(messageToBroadcast, LOG_FILE_PATH);
            broadcastMessage(messageToBroadcast);
        } catch (IOException e) {
            shutdownClient();
        }
        clients.add(this);
    }

    public BufferedWriter getOut() {
        return out;
    }

    public void broadcastMessage(String message) {
        for (ClientManager client : clients) {
            try {
                BufferedWriter writer = client.getOut();
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void shutdownClient() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
            removeClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient() {
        clients.remove(this);
        String message = "Пользователь " + userName + " вышел из чата!";
        broadcastMessage(message);
        logger.log(message, LOG_FILE_PATH);
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String message;
                message = in.readLine();
                if (message == null) {
                    shutdownClient();
                }
                if (EXIT_COMMAND.equals(message)) {
                    shutdownClient();
                    break;
                }
                broadcastMessage(userName + ": " + message);
                logger.log(message, LOG_FILE_PATH);
            } catch (IOException e) {
                shutdownClient();
                break;
            }
        }
    }
}
