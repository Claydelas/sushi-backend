package comp1206.sushi.server;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * The communication server thread that manages a thread for each new client connection. It allows a maximum of 20
 * clients to connect simultaneously (artificial limit meaning this could be adjusted to suit demand but makes it
 * easier to ensure threads are closed so they can be reused when other clients connect).
 * @author Jack Corbett
 */
class ServerComms {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private Boolean running;

    // The server can managed up to 20 simultaneous connections
    private static final int maxClientsCount = 20;
    private static final ServerThread[] threads = new ServerThread[maxClientsCount];

    /**
     * Start a thread to create CommsServerThreads for each new client connection.
     * @param server Reference to the server object
     */
    ServerComms(Server server) {
        Thread ServerThread = new Thread(() -> {
            running = true;
            int portNumber = 2222;
            try {
                serverSocket = new ServerSocket(portNumber);
                System.out.println("SUSHI SERVER STARTED");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Create a client socket for each new connection and pass it to a new client thread.
            while (running) {
                try {
                    clientSocket = serverSocket.accept();
                    int i;
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new ServerThread(clientSocket, threads, server)).start();
                            break;
                        }
                    }
                    if (i == maxClientsCount) {
//                        PrintStream os = new PrintStream(clientSocket.getOutputStream());
//                        os.println("Sushi Server is at full capacity. Please try again in a few moments");
//                        os.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        ServerThread.start();
    }
}