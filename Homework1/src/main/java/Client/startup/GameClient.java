package Client.startup;

import Client.net.ServerConnection;
import Client.view.ThreadHandling;

import java.io.IOException;

public class GameClient {

    public static void main(String[] args) throws IOException {

        ThreadHandling threadHandling = new ThreadHandling();
        ServerConnection serverConnection = new ServerConnection();

        serverConnection.connect();
        serverConnection.print();
        threadHandling.threadHandling();
        serverConnection.connection();
    }
}
