package com.lanroom;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();
    private static Map<String, Socket> userSockets = new ConcurrentHashMap<>();
    private static JTextArea logArea;
    private static JComboBox<String> userComboBox;

    public static void startServer(int port, JTextArea logArea, JComboBox<String> userComboBox) {
        ChatServer.logArea = logArea;
        ChatServer.userComboBox = userComboBox;
        logArea.append("Starting server on port " + port + "...\n");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logArea.append("Server started.\n");
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            logArea.append("Error: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void kickUser(String username) {
        Socket socket = userSockets.remove(username);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                username = in.readLine(); // First message from client is the username
                if (username == null || username.isEmpty()) {
                    socket.close();
                    return;
                }
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }
                userSockets.put(username, socket);
                userComboBox.addItem(username);
                logArea.append(username + " connected.\n");

                String message;
                while ((message = in.readLine()) != null) {
                    logArea.append("Received: " + message + "\n");
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                userSockets.remove(username);
                userComboBox.removeItem(username);
                logArea.append(username + " disconnected.\n");
            }
        }
    }
}
