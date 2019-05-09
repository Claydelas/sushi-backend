package comp1206.sushi.comms;

import comp1206.sushi.common.*;
import comp1206.sushi.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * CommunicationLayer class
 * Handles Communication between the Server and the clients
 */
public class CommunicationLayer implements Runnable {

    private static boolean commsOn = true; // Static commsOn
    /**
     * Member variables
     */
    private Server server;
    private ServerSocket serverSocket; // The Server Socket
    private ArrayList<ServerClientThread> clientList;


    /**
     * Constructor for the CommunicationLayer Class
     */
    public CommunicationLayer(Server server) {
        try {
            //Start listening on the port
            serverSocket = new ServerSocket(1432);
            this.server = server;
            clientList = new ArrayList<>();
        } catch (IOException ioe) {
            System.out.println("Failed to start server @port 1432");
            System.exit(-1);
        }
    }

    /**
     * Send message for the CommunicationLayer class
     *
     * @param message
     */
    public synchronized void sendMessage(String message) {

        System.out.println(message);

    }

    /**
     * Receive message from the CommunicationLayer class
     *
     * @param message
     * @return
     */
    public synchronized String receiveMessage(String message) {
        return message;
    }


    @Override
    public void run() {

        while (true) {

            try {

                System.out.println("CommunicationLayer waiting for connection requests...");
                Socket s = serverSocket.accept();
                System.out.println("Accepted");
                ServerClientThread currentThread = new ServerClientThread(s);
                currentThread.start();
                clientList.add(currentThread);

            } catch (IOException ioe) {

                System.out.println("Exception was found");
                ioe.printStackTrace();

            }
        }

    }


    /**
     * ServerClientThread used for every client
     */
    class ServerClientThread extends Thread {

        private User user;
        ObjectOutputStream ous;
        ObjectInputStream ois;
        private Socket s;
        private boolean isThreadOn = true;


        public ServerClientThread(Socket s) {
            this.s = s;

        }

        @Override
        public void run() {
            try {

                System.out.println("Accepted Client Address - " + s.getInetAddress().getHostName());
                ous = new ObjectOutputStream(s.getOutputStream());
                ois = new ObjectInputStream(s.getInputStream());


                while (isThreadOn) {

                    Message m = (Message) ois.readObject();
                    sendMessage(m);

                    if (!commsOn) {
                        System.out.println("Server has stopped...");
                        isThreadOn = false;
                    }
                }

                ois.close();
                ous.close();
                s.close();

                System.out.println("Stopped...");

            } catch (IOException e) {
                //  System.out.println("Client error " + e);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Send Message
         * Checks what is the message of the client
         * Responds with the appropriate message
         */
        public synchronized void sendMessage(Message sent) throws IOException {

            switch (sent.getCommand()) {
                case "GET_RESTAURANT":
                    getRestaurant();
                    break;
                case "REGISTER":
                    registerUser(sent);
                    break;
                case "LOGIN":
                    loginUser(sent);
                    break;
                case "GET_POSTCODES":
                    getPostcodes();
                    break;
                case "GET_DISHES":
                    getDishes();
                    break;
                case "GetBasket":
                    getBasket(sent);
                    break;
                case "GetBasketCost":
                    getBasketCost(sent);
                    break;
                case "AddDishBasket":
                    addDishBasket(sent);
                    break;
                case "GET_ORDERS":
                    getOrders(sent);
                    break;
                case "UpdateDishBasket":
                    updateDishBasket(sent);
                    break;
                case "ClearBasket":
                    clearBasket(sent);
                    break;
                case "CHECKOUT":
                    checkout();
                    break;
                case "OrderStatus":
                    orderStatus(sent);
                    break;
                case "OrderCost":
                    orderCost(sent);
                    break;
                case "CancelOrder":
                    cancelOrder(sent);
                    break;
                case "isOrder":
                    isOrderComplete(sent);
                    break;
            }

        }

        private void getRestaurant() throws IOException {
            ous.writeObject(new Message("Restaurant:",server.getRestaurant()));
        }

        private void registerUser(Message sent) throws IOException {
            User user = (User) sent.getObject();
            if (server.getUsers().contains(user))
                ous.writeObject(new Message("EXISTS", user));
            else {
                server.users.add(user);
                ous.writeObject(new Message("SUCCESS", null));
            }
        }

        private void loginUser(Message sent) throws IOException {
            String password = null;
            for (User user : server.getUsers()) {
                if (user.getName().equals(sent.getObject().toString())) {
                    password = user.getPassword();
                    this.user = user;
                    break;
                }
            }
            if (password != null && sent.getSecondObject().toString().equals(password))
                ous.writeObject(new Message("SUCCESS", user));
            else
                ous.writeObject(new Message("FAIL",null));
        }

        private synchronized void getPostcodes() throws IOException {
            ous.writeObject(new Message("Postcodes:", server.getPostcodes()));
        }

        private void getDishes() throws IOException {
            ous.writeObject(new Message("Dishes:", server.getDishes()));

        }

        private synchronized void getBasket(Message sent) throws IOException {

        }

        private void getBasketCost(Message sent) throws IOException {

        }

        private synchronized void addDishBasket(Message sent) throws IOException {

        }

        private synchronized void getOrders(Message sent) throws IOException {
            User user = (User) sent.getObject();
            ous.writeObject(new Message("Orders:", server.getUserOrders(user.getName())));
        }

        private synchronized void updateDishBasket(Message sent) throws IOException {

        }


        private synchronized void clearBasket(Message sent) throws IOException {

        }


        private void checkout() throws IOException {
            Order order = new Order(user, user.getBasket());
            server.orders.add(order);
            server.orderQueue.add(order);
            user.clearBasket();
            ous.writeObject(new Message("Order:", order));
        }


        private void orderStatus(Message sent) throws IOException {

        }


        private synchronized void orderCost(Message sent) throws IOException {

        }

        private synchronized void cancelOrder(Message sent) throws IOException {

        }


        private synchronized void isOrderComplete(Message sent) throws IOException {

        }

        public synchronized void subtract(User user) {

        }
    }
}

