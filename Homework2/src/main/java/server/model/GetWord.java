package server.model;

import java.io.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class GetWord {

    private File file;
    private FileInputStream inputStream;
    private BufferedReader outputStream;
    private ConcurrentHashMap<Integer, String> words;

    public GetWord(String path) {
        this.file = new File(path);
        words = new ConcurrentHashMap<>(51528);
        openFile();
        fillMap();
        close();

    }

    private boolean openFile() {
        try {
            inputStream = new FileInputStream(file);
            outputStream = new BufferedReader(new InputStreamReader(inputStream));
        } catch (FileNotFoundException e) {

            System.out.println(file.getAbsolutePath());
            return false;
        }
        return true;
    }


    /**
     * Stores all words found in file in a threadsafe map structure
     */
    private void fillMap() {

        String word = null;
        try {

            //Reader stop buffer when line is reached
            if (outputStream.ready()) {
                int i = 0;
                while ((word = outputStream.readLine()) != null) {
                    words.put(i++, word);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getRandomWord() {
        Random r = new Random();
        return words.get(r.nextInt(words.size()));
    }

    private void close() {
        try {
            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            //ignores warning
        }
    }


}