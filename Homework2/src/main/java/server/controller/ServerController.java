package server.controller;

import server.model.GetWord;

import java.io.IOException;


// Handle command from server to server
public class ServerController {

    private int attempts = 0;
    private int score = 0;
    public String currentWord;
    private String hiddenWord = new String();
    boolean found = false;
    char[] wordArray = null;
    char[] hiddenArray = null;

    private void getNewRandomWord() {
        GetWord getWord = new GetWord("C:/Users/azadm/Desktop/HW2-master/src/main/resources/words.txt");
        currentWord = getWord.getRandomWord();

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


    //HANDLES COMMAND SENT TO SERVER FROM CLIENT AND RETURNS AN INFORMATIVE STRING
    public String handleCommand(String clientCommand) {

        try {

            String msg = clientCommand;

            if (msg.contains("start")) {
                getNewRandomWord();

                return (board(hiddenArray, 2));


            } else if (msg.isEmpty()) {
                return (board(hiddenArray, 2));

            } else if ((msg.length() - 6) == currentWord.length()) {

                String temp = msg.substring(6);


                if (temp.contains(currentWord)) {
                    return (board(hiddenArray, 1));

                } else {
                    attempts--;
                    return (board(hiddenArray, 2));
                }

            } else {

                found = false;

                for (int i = 0; i < wordArray.length; i++) {
                    if (msg.charAt(6) == wordArray[i]) {
                        hiddenArray[i] = msg.charAt(6);
                        found = true;
                    }
                }

                if (currentWord.equals(String.valueOf(hiddenArray))) {
                    return (board(hiddenArray, 1));

                } else if (!found) {
                    attempts--;

                    if (attempts == 0) {
                        score--;
                        return (board(hiddenArray, 3));
                    } else {
                        return (board(hiddenArray, 2));
                    }

                } else {
                    return (board(hiddenArray, 2));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
    