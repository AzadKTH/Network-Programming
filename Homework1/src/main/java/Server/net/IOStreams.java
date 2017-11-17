package Server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IOStreams {

    private Socket clientSocket;
    private BufferedReader inputStream;
    private PrintWriter outputStream;

    boolean autoFlush = true;

    public IOStreams(Socket clientSocket) throws IOException {

        inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outputStream = new PrintWriter(clientSocket.getOutputStream(), autoFlush);

    }

    public boolean isConnected() {
        return clientSocket.isConnected();
    }

    public void sendMsg(String toSend) {
        final String str = toSend;

        Thread sendMsgThread = new Thread() {
            public void run() {
                outputStream.println(str);
                outputStream.flush();
            }
        };

        sendMsgThread.start();
    }

    public String readMsg() throws IOException {
        try{
            return inputStream.readLine();


        } catch (IOException e){

        }
        return null;
    }

    public boolean hasMsg() throws IOException {
        return inputStream.ready();
    }

    public void closeConnections() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
