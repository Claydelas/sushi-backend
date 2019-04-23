package comp1206.sushi.comms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * CommsClient used for the client side of the sushi
 */
public class CommsClient {

    private Socket s = null;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Message returnedMessage;

    private boolean isConnected = false;

    public CommsClient() {

        // Connects
        try {
            s = new Socket("127.0.0.1", 1432);
            outputStream = new ObjectOutputStream(s.getOutputStream());
            inputStream = new ObjectInputStream(s.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Message receiveMessage() {
        return returnedMessage;
    }

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
