package client.controller;

import client.net.ServerConnection;
import client.view.OutputHandler;

import java.io.IOException;

public class ClientController {
    private volatile boolean connected;
    private OutputHandler toOutput = new OutputHandler();
    private ServerConnection serverConnect;
    

    public void connect(String ipAddress, int port)throws IOException {
        serverConnect = new ServerConnection(this);
        serverConnect.connect(ipAddress, port);
        connected = true;
    }
    public void startGame(){
        if(connected){
            serverConnect.sendMsg("start");
        }
        else{
            toOutput.printLn("Not connected, type connect <address> <port> to connect");
            toOutput.print("> ");
        }
    }
    
    public void guessLetter(String letter){
        if(connected){
            serverConnect.sendMsg("guess " + letter);
        }
        else{
            toOutput.printLn("Not connected, type connect <address> <port> to connect");
            toOutput.print("> ");
        }
    }
    
    public void guessWord(String word){
        if(connected){
            serverConnect.sendMsg("guess " + word);
            //serverConnect.sendMsg("info");
        }
        else{
            toOutput.printLn("Not connected, type connect <address> <port> to connect");
            toOutput.print("> ");
        }
    }
    
    public void disconnect() throws IOException{
        if(connected){
            serverConnect.sendMsg("disconnect");
            serverConnect.disconnect();
            connected = false;
            toOutput.printLn("Disconnected");
            toOutput.print("> ");
        }
        else{
            toOutput.printLn("Not connected, type connect <address> <port> to connect");
            toOutput.print("> ");
        }
    }

    public boolean checkConnected(){
        return connected;
    }
}
