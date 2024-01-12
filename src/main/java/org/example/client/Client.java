package org.example.client;

import org.example.logger.Logger;
import org.example.settings.SettingsReader;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String userName;
    private final String EXIT_COMMAND = "/exit";
    private static final Logger logger = Logger.getInstance();
    private static final String LOG_FILE_PATH = "src/main/resources/cllog.txt";


    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.userName = userName;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            shutdownClient();
        }
    }

    private void sendMessage() {
        try {
            out.write(userName);
            out.newLine();
            out.flush();
        Scanner scanner = new Scanner(System.in);
        while (!socket.isClosed()) {
            String message = scanner.nextLine();
            out.write(message);
            out.newLine();
            out.flush();
            logger.log(message, LOG_FILE_PATH);
            if (EXIT_COMMAND.equals(message)) {
                shutdownClient();
            }
        }
        } catch (IOException e) {
            shutdownClient();
        }
    }

    private void listenToMessage() {
        new Thread(() -> {
            String messageFromServer;
            try {
                while (socket.isConnected()) {
                    messageFromServer = in.readLine();
                    if (messageFromServer == null) continue;
                    System.out.println(messageFromServer);
                    logger.log(messageFromServer, LOG_FILE_PATH);
                }
            } catch (IOException e) {
                shutdownClient();
            }
        }).start();
    }

    private void shutdownClient() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }

            logger.log(userName + " вышел из чата.", LOG_FILE_PATH);
            exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String enterYourName = "Введите свое имя:";
        System.out.println(enterYourName);
        logger.log(enterYourName, LOG_FILE_PATH);
        String userName = scanner.nextLine();
        logger.log(userName, LOG_FILE_PATH);
        while (!isUserNameCorrect(userName)) {
            String wrongName = "Некорректное имя! Имя должно быть более 2 символов, менее 13 и не быть пустым! Введите другое имя:";
            System.out.println(wrongName);
            logger.log(wrongName, LOG_FILE_PATH);
            userName = scanner.nextLine();
            logger.log(userName, LOG_FILE_PATH);
        }
        Socket socket = getSocket();
        Client client = new Client(socket, userName);
        client.listenToMessage();
        client.sendMessage();
    }

    private static Socket getSocket() throws IOException {
        SettingsReader sr = new SettingsReader();
        String host = sr.getHost();
        if (host.isEmpty()) {
            throw new IllegalArgumentException("Параметра host нет в файле настроек!)");
        }
        int port = sr.getPort();
        if (port == -1) {
            throw new IllegalArgumentException("Параметра port нет в файле настроек!)");
        }
        return new Socket(host, port);
    }

    private static boolean isUserNameCorrect(String userName) {
        return userName.length() >= 2 && userName.length() <= 13;
    }
}
