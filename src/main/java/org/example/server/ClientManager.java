package org.example.server;

import org.example.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientManager implements Runnable {
    private static final List<ClientManager> clients = new ArrayList<>();
    private static final Logger logger = Logger.getInstance();
    private final String EXIT_COMMAND = "/exit";
    private static final String LOG_FILE_PATH = "src/main/resources/srvlog.txt";

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;


    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.userName = in.readLine();
            out.println("Добро пожаловать в чат, " + userName + "!");
            String messageToBroadcast = "Пользователь " + userName + " вошел в чат!";
            logger.log(messageToBroadcast, LOG_FILE_PATH);
            broadcastMessage(messageToBroadcast);
        } catch (IOException e) {
            shutdownClient();
        }
        clients.add(this);
    }

    public PrintWriter getOut() {
        return out;
    }

    public void broadcastMessage(String message) {
        for (ClientManager client : clients) {
            client.getOut().println(message);
        }
    }

    private void shutdownClient() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
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
        while (socket.isConnected()) {
            try {
                String message;
                message = in.readLine();
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
