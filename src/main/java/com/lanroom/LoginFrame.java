package com.lanroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField serverAddressField;
    private JTextField portField;
    private JTextField usernameField;

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        panel.add(new JLabel("Server Address:"));
        serverAddressField = new JTextField("localhost");
        panel.add(serverAddressField);

        panel.add(new JLabel("Port:"));
        portField = new JTextField("12345");
        panel.add(portField);

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        JButton loginButton = new JButton("Login");
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = serverAddressField.getText();
                int port = Integer.parseInt(portField.getText());
                String username = usernameField.getText();
                ChatClient client = new ChatClient(serverAddress, port);
                if (client.connect()) {
                    ChatFrame chatFrame = new ChatFrame(client, username);
                    chatFrame.setVisible(true);
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Unable to connect to server", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(panel);
    }
}
