package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;
    private final String EXIT_COMMAND = "/exit";
    // TODO: add logger


    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.userName = userName;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            shutdownClient();
        }
    }

    private void sendMessage() {
        out.println(userName);
        Scanner scanner = new Scanner(System.in);
        while (socket.isConnected()) {
            String message = scanner.nextLine();
            out.println(message);
            if (EXIT_COMMAND.equals(message)) {
                shutdownClient();
            }
        }
    }

    private void listenToMessage() {
        new Thread(() -> {
            String messageFromServer;
            try {
                while (socket.isConnected()) {
                    messageFromServer = in.readLine();
                    System.out.println(messageFromServer);
                }
            } catch (IOException e) {
                shutdownClient();
            }
        }).start();
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
            exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println(("Введите свое имя: "));
        String userName = scanner.nextLine();
        while (!isUserNameCorrect(userName)) {
            System.out.println("Некорректное имя! Имя должно быть более 2 символов, менее 13 и не быть пустым! Введите другое имя:");
            userName = scanner.nextLine();
        }
        Socket socket = new Socket("127.0.0.1", 12345);
        Client client = new Client(socket, userName);
        client.listenToMessage();
        client.sendMessage();
    }

    private static boolean isUserNameCorrect(String userName) {
        return userName.length() >= 2 && userName.length() <= 13;
    }
}
