package client.view;

import client.controller.ClientController;
import common.Commands.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;


// Input from user, parse it and call method in ClientController
public class InputHandler implements Runnable {

    private boolean receivingCmds = false;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String command;
    String rest;
    ClientController clientController;

    //START THREAD FOR RUN METHOD
    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
        clientController = new ClientController();
    }

    //RUN METHOD PROCESSES INPUT FROM USER
    @Override
    public void run() {
        System.out.println("Welcome to Hangman. Type 'connect <address> 8080' to connect, then 'start' to start playing");
        System.out.print("> ");
        while (receivingCmds) {
            try {
                String clientInput = reader.readLine();
                int i = clientInput.indexOf(' ');
                Command cmd;
                if (i < 0) {
                    if (clientInput.equals("disconnect")) {
                        cmd = Command.DISCONNECT;
                    } else if (clientInput.equals("start")) {
                        cmd = Command.START_GAME;
                    } else {
                        cmd = Command.UNKNOWN;
                    }
                } else {
                    command = clientInput.substring(0, i);
                    rest = clientInput.substring(i + 1);
                    cmd = getCommand(command, rest);
                }
                switch (cmd) {
                    case CONNECT:

                        int j = rest.indexOf(' ');
                        String address = rest.substring(0, j);
                        String port = rest.substring(j + 1);
                        clientController.connect(address, Integer.parseInt(port));
                        System.out.println("Connecting " + address + " " + port);
                        System.out.print("> ");
                        break;
                    case DISCONNECT:
                        System.out.println("Sending disconnect command");
                        clientController.disconnect();
                        break;
                    case START_GAME:
                        if (clientController.checkConnected()) {
                            System.out.println("Starting game- type 'guess' followed by a letter or word");
                            clientController.startGame();
                            System.out.print("> ");
                        } else {
                            System.out.println("Not connected");
                            System.out.print("> ");
                        }
                        break;
                    case GUESS_LETTER:
                        if (clientController.checkConnected()) {
                            System.out.println("Guessing letter " + rest);
                            clientController.guessLetter(rest);
                            System.out.print("> ");
                        } else {
                            System.out.println("Not connected");
                            System.out.print("> ");
                        }
                        break;
                    case GUESS_WORD:
                        if (clientController.checkConnected()) {
                            System.out.println("Guessing word");
                            clientController.guessWord(rest);
                            System.out.print("> ");
                        } else {
                            System.out.println("Not connected");
                            System.out.print("> ");
                        }
                        break;
                    case UNKNOWN:
                        System.out.println("Invalid command");
                        System.out.print("> ");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
                e.printStackTrace();
                System.out.print("> ");
            }
        }
    }

    //DETERMINE ENUM COMMAND BASED ON INPUT FROM USER
    private Command getCommand(String command, String rest) {
        Command returnCommand;
        if (command.equals("connect")) {
            returnCommand = Command.CONNECT;
        } else if (command.equals("guess")) {
            if (rest.length() > 1) {
                returnCommand = Command.GUESS_WORD;
            } else {
                returnCommand = Command.GUESS_LETTER;
            }
        } else {
            returnCommand = Command.UNKNOWN;
        }
        return returnCommand;
    }
}

