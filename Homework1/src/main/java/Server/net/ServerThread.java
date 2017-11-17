package Server.net;

import Server.model.GetWord;
import Server.model.HangmanGame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

    private static ServerSocket serverSocket;
    public static Socket clientSocket;
    private static int PORT = 8080;
    private GetWord getWord;

    public ServerThread(int PORT) throws IOException {
        ServerThread.PORT = PORT;
        serverSocket = new ServerSocket(PORT);
        //C:\Users\azadm\Google Drive\Sync\Documents\courses\Year 2\Period 2\Network Programming\Homeworks\Homework1\src\main\java\Server\model
        getWord = new GetWord("C:/Users/azadm/Google Drive/Sync/Documents/courses/Year 2/Period 2/Network Programming/Homeworks/Homework1/src/main/java/Server/model/words.txt");
    }

    public void run() {
        System.out.println("Server is up and waiting for connections on port: " + PORT);

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                if (clientSocket != null) {
                    // Start new thread for each client
                    new HangmanGame(getWord, clientSocket).start();
                }
            } catch (IOException ignored) {
            }
        }
    }


    public void interrupt() {
        try {
            serverSocket.close();
        } catch (IOException e) {

        }
    }


}
