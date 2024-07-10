package com.lanroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;

public class ChatFrame extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private ChatClient client;
    private String username;

    public ChatFrame(ChatClient client, String username) {
        this.client = client;
        this.username = username;
        setTitle("Chat Room");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                if (!message.trim().isEmpty()) {
                    client.sendMessage(username + ": " + message);
                    inputField.setText("");
                }
            }
        });

        new Thread(new IncomingMessageHandler(client.getIn())).start();
    }

    private class IncomingMessageHandler implements Runnable {
        private BufferedReader in;

        public IncomingMessageHandler(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
//提交再次失败