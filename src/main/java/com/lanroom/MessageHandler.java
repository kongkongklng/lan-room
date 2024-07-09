package com.lanroom;

import java.io.BufferedReader;
import java.io.IOException;

public class MessageHandler implements Runnable {
    private BufferedReader in;

    public MessageHandler(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
