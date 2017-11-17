package Server.net;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class GameServer {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static final int PORT = 8080;

    public void runServer() throws IOException {

        try{
            ServerThread serverSocket = new ServerThread(PORT);
            serverSocket.start(); // Start thread to wait for new clients

            Scanner sc = new Scanner(System.in);
            while (true){
                String input = sc.nextLine();
                if(input.equals("exit")){
                    serverSocket.interrupt(); // Stops all threads
                    System.out.println("Closing server");
                }
            }

        }catch (BindException e){
            System.out.println("Port already in use, closing!");
        } catch (IOException ignored){

        }

    }

    public static void main(String[] args) throws IOException {

        new GameServer().runServer();

    }
}