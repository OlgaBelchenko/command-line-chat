package org.example.client;

import org.example.logger.Logger;
import org.example.settings.SettingsReader;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String userName;
    private final String EXIT_COMMAND = "/exit";
    private final Logger logger = Logger.getInstance();
    private final String LOG_FILE_PATH = "src/main/resources/client_log.txt";
    private final String SETTINGS_FILE_PATH = "src/main/resources/settings.txt";
    private final String PORT_PARAMETER = "port";
    private final String HOST_PARAMETER = "host";


    public Client() {
        try {
            userName = getUserNameFromUser();
            socket = getSocketFromFile();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public Client(Socket socket) {
        try {
            userName = getUserNameFromUser();
            this.socket = socket;
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
                    System.out.println(getCurrentTime() + messageFromServer);
                    logger.log(messageFromServer, LOG_FILE_PATH);
                }
            } catch (IOException e) {
                shutdownClient();
            }
        }).start();
    }

    private String getCurrentTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String dateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(timestamp);
        return dateTime + " ";
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

    private Socket getSocketFromFile() throws IOException {
        SettingsReader sr = new SettingsReader(SETTINGS_FILE_PATH);
        String host = sr.getSetting(HOST_PARAMETER);
        if (host.isEmpty()) {
            throw new IllegalArgumentException("Параметра host нет в файле настроек!)");
        }
        String port = sr.getSetting(PORT_PARAMETER);
        if ("".equals(port)) {
            throw new IllegalArgumentException("Параметра port нет в файле настроек!)");
        }

        return new Socket(host, Integer.parseInt(port));
    }

    private boolean isUserNameCorrect(String userName) {
        return userName.length() >= 2 && userName.length() <= 13;
    }

    public void start() {
//        userName = getUserNameFromUser();
        listenToMessage();
        sendMessage();
    }

    private String getUserNameFromUser() {
        Scanner scanner = new Scanner(System.in);
        String enterYourName = "Введите свое имя:";
        System.out.println(enterYourName);
        logger.log(enterYourName, LOG_FILE_PATH);
        String name = scanner.nextLine();
        logger.log(name, LOG_FILE_PATH);
        while (!isUserNameCorrect(name)) {
            String wrongName = "Некорректное имя! Имя должно быть более 2 символов, менее 13 и не быть пустым! Введите другое имя:";
            System.out.println(wrongName);
            logger.log(wrongName, LOG_FILE_PATH);
            name = scanner.nextLine();
            logger.log(name, LOG_FILE_PATH);
        }
        return name;
    }
}
