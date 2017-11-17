package Client.net;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;

// NET Pack

public class ServerConnection {
    private static final int PORT = 8080;
    private static volatile Socket socket = null;
    static private volatile boolean running;


    private static volatile PrintWriter outputStream = null;
    private static volatile BufferedReader inputStream = null;

    public void connect(){
        try {
            socket = new Socket("127.0.0.1", PORT);
            boolean autoFlush = true;
            outputStream = new PrintWriter(socket.getOutputStream(), autoFlush);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Couldn't find host!");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(){
        System.out.println("Welcome to the hangman game!");
        System.out.println();
        System.out.println("Type 'start' to play the game or 'quit' to exit the game!");
        running = true;

    }

    public void sendToServer(String input){
        outputStream.println(input);
    }

    public void connection() throws IOException {
        while (socket.isConnected() && running) {

            String fromServer = inputStream.readLine();

            if (fromServer != null) {
                System.out.println(fromServer);
            } else {
                running = true;

            }
        }
    }



    public void diconnect () throws IOException {
        outputStream.close();
        inputStream.close();
        socket.close();

    }




}