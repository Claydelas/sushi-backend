package comp1206.sushi.comms;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Handles communicating with the server using string based messages and performs relevant updates to the client
 * depending on the response.
 * @author Jack Corbett
 */
public class ClientSocket {
    private Socket clientSocket = null;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> responseQueue = new LinkedBlockingQueue<>();

    /**
     * Constructor which starts a new thread to establish a connection to the server
     */
    public ClientSocket() {
        Thread ClientThread = new Thread(() -> {
            try {
                clientSocket = new Socket("127.0.0.1", 2222);
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
            } catch (UnknownHostException e) {
                System.err.println("Cannot find server");
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to server");
            }

            if (clientSocket != null && outputStream != null && inputStream != null) {
                try {
                    System.out.println("SUSHI CLIENT STARTED");

                    while(true) {
                        outputStream.writeObject(messageQueue.take());
                        responseQueue.add((Message) inputStream.readObject());
                    }
                } catch (UnknownHostException e) {
                    System.err.println("Trying to connect to unknown host: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("IOException:  " + e.getMessage());
                } catch (InterruptedException e) {
                    System.err.println("Interrupted Exception: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        ClientThread.start();
    }

    /**
     * Sends a message to the server by adding it to the message queue.
     * @param message String to be sent
     */
    public void sendMessage(Message message) {
        messageQueue.add(message);
        try {
            responseQueue.add((Message) inputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Relieves a message from the server by checking the response queue, waiting up to a second for the response. This
     * ensures the correct response is received without the thread being blocked if there is an issue with the server.
     * @return The server's response.
     */
    public Message receiveMessage() {
        try {
            return responseQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted - Unable to receive response from server");
        }
        System.err.println("Timeout - No response from server");
        return null;
    }
}
