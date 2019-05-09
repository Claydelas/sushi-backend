package comp1206.sushi.server;

import comp1206.sushi.common.*;
import comp1206.sushi.comms.Message;

import java.io.*;
import java.net.Socket;

/**
 * A communication thread that is started per client connection and handles acting upon the messages sent by the client
 * and responding.
 *
 * @author Jack Corbett
 */
class ServerThread extends Thread {

    private final ServerThread[] threads;
    private ObjectOutputStream ous;
    private Server server;
    private Socket clientSocket;
    private int maxClientsCount;
    private boolean isThreadOn = true;
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

        int maxClientsCount = this.maxClientsCount;
        ServerThread[] threads = this.threads;

        try {
            System.out.println("Accepted Client Address - " + clientSocket.getInetAddress().getHostName());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ous = new ObjectOutputStream(clientSocket.getOutputStream());

            System.out.println("CLIENT CONNECTED");

            // Loop while lines are being returned over the socket
            while (isThreadOn) {
                sendMessage((Message) ois.readObject());
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            ois.close();
            ous.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private synchronized void sendMessage(Message sent) throws IOException {

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
            case "LOGOFF":
                logoff();
                break;
        }

    }

    private void logoff() {
        isThreadOn = false;
    }

    private void getRestaurant() throws IOException {
        ous.writeObject(new Message("Restaurant:", server.getRestaurant()));
    }

    private void registerUser(Message sent) throws IOException {
        User user = (User) sent.getObject();
        Postcode userPostcode = null;
        for (Postcode postcode : server.getPostcodes()) {
            if (postcode.getName().equals(user.getPostcode().getName())) {
                userPostcode = postcode;
                break;
            }
        }
        if (userPostcode != null && !server.getUsers().contains(user)) {
            server.users.add(user);
            this.user = user;
            ous.writeObject(new Message("SUCCESS", user));
        } else {
            ous.writeObject(new Message("EXISTS", null));
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
            ous.writeObject(new Message("FAIL", null));
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
        ous.writeObject(new Message("Orders:", server.getUserOrders(user)));
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
//        if (command[0].startsWith("USER")) {
//
//                    if (command[0].contains("REGISTER")) {
//                        Postcode userPostcode = null;
//                        for (Postcode postcode : server.getPostcodes()) {
//                            if (postcode.getName().equals(command[4])) {
//                                userPostcode = postcode;
//                                break;
//                            }
//                        }
//                        if (userPostcode != null) {
//                            User user = new User(command[1], command[2], command[3], userPostcode);
//                            server.users.add(user);
//                            os.writeObject(user);
//                        } else {
//                            os.writeObject("FAIL");
//                        }
//                    }
//                    if (command[0].contains("LOGIN")) {
//                        String password = null;
//                        // Find the password for that user to check it is correct
//                        for (User user : server.users) {
//                            if (user.getName().equals(command[1])) {
//                                password = user.getPassword();
//                                /* Set the reference to the user of this class (this saves searching for the user each
//                                time as only one user can use a thread at a time) */
//                                this.user = user;
//                                break;
//                            }
//                        }
//                        if (password != null && password.equals(command[2])) {
//                            os.writeObject(user);
//                        } else {
//                            os.writeObject("FAIL");
//                        }
//                    }
//                } else if (command[0].startsWith("GET")) {
//                    if (command[0].contains("RESTAURANT")) {
//                        if (command[0].contains("RESTAURANT_NAME")) os.writeObject(server.getRestaurantName());
//                        else if (command[0].contains("RESTAURANT_LOCATION"))
//                            os.writeObject(server.getRestaurantPostcode());
//                        else os.writeObject(server.getRestaurant());
//                    }
//                    if (command[0].contains("POSTCODES")){
//                        os.writeObject(server.getPostcodes());
//                    }
//                    if (command[0].contains("DISHES")){
//                        os.writeObject(server.getDishes());
//                    }
//                    if (command[0].contains("ORDERS")){
//                        os.writeObject(server.getUserOrders(new User("hi","h","i",new Postcode("SO16 3ZE"))));
//                    }
//                } else if (command[0].startsWith("BASKET")) {
//                    Order order = new Order(user, user.getBasket());
//                    server.orders.add(order);
//                    server.orderQueue.add(order);
//                    user.clearBasket();
//                    os.writeObject(order);
//                }
//            }
//            // When the client disconnects free up the space in the thread array so others can connect
//            synchronized (this) {
//                for (int i = 0; i < maxClientsCount; i++) {
//                    if (threads[i] == this) {
//                        threads[i] = null;
//                    }
//                }
//            }
//
//            is.close();
//            os.close();
//            //clientSocket.close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}
