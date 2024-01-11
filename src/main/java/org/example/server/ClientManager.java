package org.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientManager implements Runnable {
    public static List<ClientManager> clients = new ArrayList<>();
    private final String EXIT_COMMAND = "/exit";
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;
    // TODO: add logger

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.userName = in.readLine();
            out.println("Добро пожаловать в чат, " + userName + "!");
            // TODO: log
            String messageToBroadcast = "Пользователь " + userName + " вошел в чат!";
            broadcastMessage(messageToBroadcast);
            // TODO: log
            logMessage(messageToBroadcast);
        } catch (IOException e) {
            shutdownClient();
        }
        clients.add(this);
    }

    public PrintWriter getOut() {
        return out;
    }

    public void broadcastMessage(String message) {
        if (clients != null) {
            for (ClientManager client : clients) {
                client.getOut().println(message);
            }
        }
    }

    private void logMessage(String message) {
        // TODO
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
        logMessage(message);
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
                logMessage(message);
            } catch (IOException e) {
                shutdownClient();
                break;
            }
        }
    }
}
