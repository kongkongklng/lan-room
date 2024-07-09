package com.lanroom;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.Set;
import java.util.HashSet;

public class ServerFrame extends JFrame {
    private JTextField portField;
    private JTextArea logArea;
    private JComboBox<String> userComboBox;
    private JButton kickButton;

    public ServerFrame() {
        setTitle("Server");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 2));

        topPanel.add(new JLabel("IP Address:"));
        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            topPanel.add(new JLabel(ipAddress));
        } catch (Exception e) {
            topPanel.add(new JLabel("Unable to get IP address"));
        }

        topPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345");
        topPanel.add(portField);

        panel.add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        userComboBox = new JComboBox<>();
        bottomPanel.add(userComboBox);

        kickButton = new JButton("Kick User");
        kickButton.addActionListener(e -> kickUser());
        bottomPanel.add(kickButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        JButton startButton = new JButton("Start Server");
        startButton.addActionListener(e -> startServer());
        panel.add(startButton, BorderLayout.SOUTH);

        add(panel);
    }

    private void startServer() {
        int port = Integer.parseInt(portField.getText());
        new Thread(() -> ChatServer.startServer(port, logArea, userComboBox)).start();
    }

    private void kickUser() {
        String selectedUser = (String) userComboBox.getSelectedItem();
        if (selectedUser != null) {
            ChatServer.kickUser(selectedUser);
            logArea.append("Kicked user: " + selectedUser + "\n");
            userComboBox.removeItem(selectedUser);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerFrame serverFrame = new ServerFrame();
            serverFrame.setVisible(true);
        });
    }
}
