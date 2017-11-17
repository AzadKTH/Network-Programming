package Server.model;

import Server.net.IOStreams;

import java.io.IOException;
import java.net.Socket;

public class HangmanGame extends Thread {

    private IOStreams ioStreams;
    private GetWord word;
    private int attempts = 0;
    private int score = 0;
    public String currentWord;
    private String hiddenWord = new String();
    boolean found = false;
    char[] wordArray = null;
    char[] hiddenArray = null;


    public HangmanGame(GetWord getWord, Socket clientSocket) throws IOException {

        ioStreams = new IOStreams(clientSocket);
        word = getWord;
    }

    private void getNewRandomWord() {
        currentWord = word.getRandomWord();
        System.out.println("Chosen word for client connected: " + currentWord);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentWord.length(); i++) {
            sb.append("-");
        }

        hiddenWord = sb.toString();

        wordArray = currentWord.toCharArray();
        hiddenArray = hiddenWord.toCharArray();

        attempts = currentWord.length();
    }

    private String board(char[] hiddenWord, int win) throws IOException {
        String hidden = String.valueOf(hiddenWord);

        if (win == 1) {
            score++;
            return currentWord + " You've won! Your score is now: " + score + "\n'start' to play again or 'quit' to exit!";

        } else if (win == 2) {

            return "Word: " + hidden + "\tLengthOfWord: " + currentWord.length() + "\tAttempts left: " + attempts + "\tScore: " + score;

        } else {
            return "You've lost \t The word was: " + currentWord + " \t Current score: "
                    + score + "\n'Start' to play again! or 'quit' to exit!";
        }
    }


    public void run() {

        while (!isInterrupted()) {

            try {

                if (ioStreams.hasMsg()) {

                    String msg = ioStreams.readMsg();

                    if (msg.contains("start")) {
                        getNewRandomWord();

                        ioStreams.sendMsg(board(hiddenArray, 2));

                    } else if (msg.isEmpty()) {
                        ioStreams.sendMsg(board(hiddenArray, 2));

                    } else if (msg.length() == currentWord.length()) {

                        if (msg.contains(currentWord)) {
                            ioStreams.sendMsg(board(hiddenArray, 1));

                        } else {
                            attempts--;
                            ioStreams.sendMsg(board(hiddenArray, 2));
                        }

                    } else {

                        found = false;

                        for (int i = 0; i < wordArray.length; i++) {
                            if (msg.charAt(0) == wordArray[i]) {
                                hiddenArray[i] = msg.charAt(0);
                                found = true;
                            }
                        }

                        if (currentWord.equals(String.valueOf(hiddenArray))) {
                            ioStreams.sendMsg(board(hiddenArray, 1));

                        } else if (!found) {
                            attempts--;

                            if (attempts == 0) {
                                score--;
                                ioStreams.sendMsg(board(hiddenArray, 3));
                            } else {
                                ioStreams.sendMsg(board(hiddenArray, 2));
                            }

                        } else {
                            ioStreams.sendMsg(board(hiddenArray, 2));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}