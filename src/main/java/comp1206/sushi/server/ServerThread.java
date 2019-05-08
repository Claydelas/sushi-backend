package comp1206.sushi.server;

import comp1206.sushi.common.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A communication thread that is started per client connection and handles acting upon the messages sent by the client
 * and responding.
 *
 * @author Jack Corbett
 */
class ServerThread extends Thread {

    private final ServerThread[] threads;
    private Server server;
    private Socket clientSocket;
    private int maxClientsCount;
    // Stores a reference to the user of this client thread
    private User user;

    /**
     * Creates a new server thread, setting it's properties
     *
     * @param clientSocket The socket used for communication with the client
     * @param threads      The server thread array
     * @param server       Reference to the server object
     */
    ServerThread(Socket clientSocket, ServerThread[] threads, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    /**
     * When the server thread us run, setup the connection, read each message sent by the client and send back a
     * response. Taking action by calling methods on the server where necessary.
     */
    public void run() {
        ObjectOutputStream os;
        InputStream is;
        BufferedReader ir;

        int maxClientsCount = this.maxClientsCount;
        ServerThread[] threads = this.threads;

        try {
            is = clientSocket.getInputStream();
            ir = new BufferedReader(new InputStreamReader(is));
            os = new ObjectOutputStream(clientSocket.getOutputStream());

            System.out.println("CLIENT CONNECTED");

            String message;
            // Loop while lines are being returned over the socket
            while ((message = ir.readLine()) != null) {
                String[] command = message.split(":");

                if (command[0].startsWith("USER")) {

                    if (command[0].contains("REGISTER")) {
                        Postcode userPostcode = null;
                        for (Postcode postcode : server.getPostcodes()) {
                            if (postcode.getName().equals(command[4])) {
                                userPostcode = postcode;
                                break;
                            }
                        }
                        if (userPostcode != null) {
                            User user = new User(command[1], command[2], command[3], userPostcode);
                            server.users.add(user);
                            os.writeObject(user);
                        } else {
                            os.writeObject("FAIL");
                        }
                    }
                    if (command[0].contains("LOGIN")) {
                        String password = null;
                        // Find the password for that user to check it is correct
                        for (User user : server.users) {
                            if (user.getName().equals(command[1])) {
                                password = user.getPassword();
                                /* Set the reference to the user of this class (this saves searching for the user each
                                time as only one user can use a thread at a time) */
                                this.user = user;
                                break;
                            }
                        }
                        if (password != null && password.equals(command[2])) {
                            os.writeObject(user);
                        } else {
                            os.writeObject("FAIL");
                        }
                    }
                } else if (command[0].startsWith("GET")) {
                    if (command[0].contains("RESTAURANT")) {
                        if (command[0].contains("RESTAURANT_NAME")) os.writeObject(server.getRestaurantName());
                        else if (command[0].contains("RESTAURANT_LOCATION"))
                            os.writeObject(server.getRestaurantPostcode());
                        else os.writeObject(server.getRestaurant());
                    }
                    if (command[0].contains("POSTCODES")){
                        os.writeObject(server.getPostcodes());
                    }
                    if (command[0].contains("DISHES")){
                        os.writeObject(server.getDishes());
                    }
                    if (command[0].contains("ORDERS")){
                        os.writeObject(server.getUserOrders(new User("hi","h","i",new Postcode("SO16 3ZE"))));
                    }
                } else if (command[0].startsWith("BASKET")) {
                    Order order = new Order(user, user.getBasket());
                    server.orders.add(order);
                    server.orderQueue.add(order);
                    user.clearBasket();
                    os.writeObject(order);
                }
            }
            // When the client disconnects free up the space in the thread array so others can connect
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            is.close();
            os.close();
            //clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
