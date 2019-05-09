package comp1206.sushi.comms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientComms {

    // for I/O
    private ObjectInputStream sInput;        // to read from the socket
    private ObjectOutputStream sOutput;        // to write on the socket
    private Socket socket;                    // socket object

    private String serverIP;    // server and username
    private int port;                    //port
    private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Object> responseQueue = new LinkedBlockingQueue<>();


    /*
     *  Constructor to set below things
     *  server: the server address
     *  port: the port number
     *  username: the username
     */

    public ClientComms(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
        this.start();
    }

    /*
     * To start the chat
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(serverIP, port);
        }
        // exception handler if it failed
        catch (Exception ec) {
            System.out.println("Error connecting to server:" + ec);
            return false;
        }
        System.out.println("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            System.out.println("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();

        return true;
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect
     */
    public void disconnect() {
        try {
            if (sOutput != null) sOutput.close();
            if (sInput != null) sInput.close();
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        }
    }

    /*
     * To send a message to the server
     */
    public void sendMessage(Message msg) {
        try {
            messageQueue.add(msg);
            sOutput.writeObject(messageQueue.take());
        } catch (IOException e) {
            System.out.println("Exception writing to server: " + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Object receiveMessage() {
        try {
            return responseQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted - Unable to receive response from server");
        }
        System.err.println("Timeout - No response from server");
        return null;
    }

    /*
     * a class that waits for the message from the server
     */
    class ListenFromServer extends Thread {

        public void run() {
            while (true) {
                try {
                    Message response = (Message) sInput.readObject();
                    // read the message form the input datastream
                    responseQueue.add(response.getResponse());
                } catch (IOException e) {
                    System.out.println("Server has closed the connection: " + e);
                    break;
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }
}
