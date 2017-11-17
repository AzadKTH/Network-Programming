package Client.view;

import Client.net.ServerConnection;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ThreadHandling {

    static private volatile boolean running = true;

    ServerConnection connect = new ServerConnection();


    public void threadHandling() throws IOException {

        //Create a new thread
        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (running) {
                        Scanner sc = new Scanner(System.in);
                        String input = sc.nextLine();

                        // Quit If 'quit' is the character
                        if (input.equals("quit")) {
                            running = false;
                        } else {
                            //outputStream.println(input);
                            connect.sendToServer(input);
                        }
                    }
                } finally {
                    interrupt();
                }
            }
        });
        clientThread.start();
    }

    private void interrupt() {
        try {
            connect.diconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}
