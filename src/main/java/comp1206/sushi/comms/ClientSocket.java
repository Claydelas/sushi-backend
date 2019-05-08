package comp1206.sushi.comms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket {
    /**
     * Member variables
     */
    private Socket s = null;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Message returnedMessage;

    private boolean isConnected = false;


    /**
     * Constructor
     */
    public ClientSocket() {

        // Connects
        try {
            s = new Socket("127.0.0.1", 1432);
            outputStream = new ObjectOutputStream(s.getOutputStream());
            inputStream = new ObjectInputStream(s.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


    /**
     * Receive Message
     * @return
     */
    public Message receiveMessage() {

        return returnedMessage;

    }

    /**
     * Send message
     * @param message
     */
    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            returnedMessage = (Message) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
