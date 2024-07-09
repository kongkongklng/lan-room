package com.lanroom;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static JTextArea logArea;

    public static void startServer(int port, JTextArea logArea) {
        ChatServer.logArea = logArea;
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

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }
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
            }
        }
    }
}
