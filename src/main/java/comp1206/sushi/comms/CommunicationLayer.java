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

    /**
     * Member variables
     */
    private Server server;
    private User user;
    private ServerSocket serverSocket; // The Server Socket
    private static boolean commsOn = true; // Static commsOn

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

        private Socket s;
        private boolean isThreadOn = true;


        public ServerClientThread(Socket s) {
            this.s = s;

        }

        @Override
        public void run() {
            try {

                System.out.println("Accepted Client Address - " + s.getInetAddress().getHostName());
                ObjectOutputStream ous = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());


                while (isThreadOn) {

                    Message m = (Message) ois.readObject();
                    sendMessage(m, ous);

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
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        public synchronized void sendMessage(Message sent, ObjectOutputStream ous) throws IOException {

            if (sent.getCommand().equals("Register")) {
                registerUser(sent, ous);
            } else if (sent.getCommand().equals("Login")) {
                loginUser(sent, ous);
            } else if (sent.getCommand().equals("GetPostcodes")) {
                getPostcodes(sent, ous);

            } else if (sent.getCommand().equals("GetDishes")) {
                getDishes(sent, ous);

            } else if (sent.getCommand().equals("GetBasket")) {
                getBasket(sent, ous);

            } else if (sent.getCommand().equals("GetBasketCost")) {
                getBasketCost(sent, ous);
            } else if (sent.getCommand().equals("AddDishBasket")) {
                addDishBasket(sent, ous);
            } else if (sent.getCommand().equals("GetOrders")) {
                getOrders(sent, ous);
            } else if (sent.getCommand().equals("UpdateDishBasket")) {
                updateDishBasket(sent, ous);
            } else if (sent.getCommand().equals("ClearBasket")) {
                clearBasket(sent, ous);
            } else if (sent.getCommand().equals("CheckoutBasket")) {
                checkout(sent, ous);
            } else if (sent.getCommand().equals("OrderStatus")) {
                orderStatus(sent, ous);
            } else if (sent.getCommand().equals("OrderCost")) {
                orderCost(sent, ous);
            } else if (sent.getCommand().equals("CancelOrder")) {
                cancelOrder(sent, ous);
            } else if (sent.getCommand().equals("isOrder")) {
                isOrderComplete(sent, ous);
            }

        }

        /**
         * Register user message
         *
         * @param sent
         * @param ous
         */
        private void registerUser(Message sent, ObjectOutputStream ous) {
            User user = (User) sent.getObject();
            if (availableUsers.containsKey(user.getName())) {
                try {
                    Message failMessage = new Message("FailRegister", null);
                    ous.writeObject(failMessage);
                    ous.flush();
                    System.out.println("Fail");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    System.out.println("User " + user.getName());
                    Message registerMessage = new Message("SuccessRegister", null);
                    ous.writeObject(registerMessage);
                    ous.flush();
                    availableUsers.put(user.getName(), user);
                    users.add(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        /**
         * Login user message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private void loginUser(Message sent, ObjectOutputStream ous) throws IOException {

            UserLogin userLogin = (UserLogin) sent.getObject();
            String name = userLogin.getName();
            String password = userLogin.getPassword();
            if (availableUsers.containsKey(name)) {
                User c = availableUsers.get(name);
                if (c.getPassword().equals(password)) {
                    Message m = new Message("SuccessLogin", c);
                    ous.writeObject(m);
                    ous.flush();

                } else {

                    Message m = new Message("Fail", null);
                    ous.writeObject(m);
                    ous.flush();
                }
            } else {
                Message m1 = new Message("Fail", null);
                ous.writeObject(m1);
                ous.flush();

            }
        }

        /**
         * Get postcodes message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void getPostcodes(Message sent, ObjectOutputStream ous) throws IOException {

            Message m = new Message("postcodes", postcodes);


            ous.writeObject(m);
            ous.flush();

        }

        /**
         * Get dishes message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private void getDishes(Message sent, ObjectOutputStream ous) throws IOException {

            Message m = new Message("dishes", dishes);
            ous.writeObject(m);
            ous.flush();
        }

        /**
         * Get Basket message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void getBasket(Message sent, ObjectOutputStream ous) throws IOException {

            User user = (User) sent.getObject();

            if (availableUsers.containsKey(user.getName())) {
                HashMap<Dish, Number> dishesUser = availableUsers.get(user.getName()).getBasket();

                ous.reset();
                Message m = new Message("SuccessBasket", dishesUser);
                ous.writeObject(m);
                ous.flush();

            } else {
                Message m = new Message("Fail", null);
                ous.writeObject(m);
                ous.flush();
            }


        }


        /**
         * Get basket message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private void getBasketCost(Message sent, ObjectOutputStream ous) throws IOException {

            User user = (User) sent.getObject();

            if (availableUsers.containsKey(user.getName())) {

                HashMap<Dish, Number> dishesInBasket = availableUsers.get(user.getName()).getBasket();
                Integer sum = 0;

                for (Map.Entry<Dish, Number> entry : dishesInBasket.entrySet()) {
                    sum = sum + entry.getKey().getPrice() * (Integer) entry.getValue();
                }


                Message returnedMessage = new Message("SuccessBasketCost", sum);
                ous.writeObject(returnedMessage);
                ous.flush();

            } else {
                Message returnedMessage = new Message("FailBasketCost", null);
                ous.writeObject(returnedMessage);
                ous.flush();

            }
        }

        /**
         * Add dish basket message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void addDishBasket(Message sent, ObjectOutputStream ous) throws IOException {

            String messageString = sent.getObject().toString();
            String userName = messageString.split("\\|")[0];

            if (availableUsers.containsKey(userName)) {

                User user = availableUsers.get(userName);
                HashMap<Dish, Number> userBasket = user.getBasket();

                String dishName = messageString.split("\\|")[1];
                Integer quantity = Integer.parseInt(messageString.split("\\|")[2]);
                Dish dishBasket = null;

                for (Dish currentDish : dishes) {
                    if (currentDish.getName().equals(dishName)) {
                        dishBasket = currentDish;
                        break;
                    }
                }

                user.addToBasket(dishBasket, quantity);
                Message returnedMessage = new Message("Success", null);

                ous.writeObject(returnedMessage);
                ous.flush();

                user.getBasket().put(dishBasket, quantity);

            }

        }

        /**
         * Get Orders message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void getOrders(Message sent, ObjectOutputStream ous) throws IOException {

            User user = (User) sent.getObject();
            User getUser = availableUsers.get(user.getName());

            ArrayList<Order> ordersOfUser = new ArrayList<>();


            for (Order currentOrder : orders) {

                if (currentOrder.getName().equals(getUser.getName())) ordersOfUser.add(currentOrder);
            }


            Message message = new Message("Success", ordersOfUser);

            for (Order currentOrders : ordersOfUser) {
                System.out.println(currentOrders.getName() + " order");
                HashMap<Dish, Number> od = currentOrders.getOrders();
            }

            ous.reset();
            ous.writeObject(message);
            ous.flush();

        }

        /**
         * Update dish basket
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void updateDishBasket(Message sent, ObjectOutputStream ous) throws IOException {

            User user = (User) sent.getObject();
            User getUser = availableUsers.get(user.getName());

            Dish dish = (Dish) sent.getSecondObject();
            Dish dishBasket = null;


            for (Dish currentDish : dishes) {
                if (currentDish.getName().equals(dish.getName())) {
                    dishBasket = currentDish;
                    break;
                }
            }

            Number quantity = (Number) sent.getThirdObject();
            getUser.addToBasket(dishBasket, quantity);

            ous.writeObject(new Message("Successfully updated", null));
            ous.flush();
        }

        /**
         * Clear basket
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void clearBasket(Message sent, ObjectOutputStream ous) throws IOException {

            User user = (User) sent.getObject();
            User getUser = availableUsers.get(user.getName());
            getUser.clearBasket();

            ous.writeObject(new Message("Successfully cleared basket", null));
            ous.flush();

        }

        /**
         * Checkout message
         *
         * @param sent
         * @param ous
         * @throws IOException
         */
        private void checkout(Message sent, ObjectOutputStream ous) throws IOException {

            User user = (User) sent.getObject();
            User getUser = availableUsers.get(user.getName());

            Boolean hasEnough = true;
            String description = "";
            for (Map.Entry<Dish, Number> entry : getUser.getBasket().entrySet()) {
                description = entry.getValue() + " * " + entry.getKey().getName() + ",";
                if ((Integer) entry.getValue() > (Integer) inventory.getDishes().get(entry.getKey())) {

                    hasEnough = false;
                    break;
                }
            }

            if (hasEnough) {

                description = description.substring(0, description.length() - 1);
                subtract(getUser);
                Integer id = 0;

                if (ordersId.containsKey(user.getName())) {
                    id = ordersId.get(user.getName());
                }
                id++;
                ordersId.put(user.getName(), id);
                Order orderOfUser = new Order(getUser, getUser.getBasket(), description, id);
                orders.add(orderOfUser);

                Message returnedMessage = new Message("Success", orderOfUser);
                ous.writeObject(returnedMessage);
                ous.flush();
            } else {
                Message returnedMessage = new Message("Fail", null);
                ous.writeObject(returnedMessage);
                ous.flush();
            }


        }

        /**
         * Order status message
         * Getts the order status
         * @param sent
         * @param ous
         * @throws IOException
         */
        private void orderStatus(Message sent, ObjectOutputStream ous) throws IOException {

            String status = "";

            Order orderToCheck = (Order) sent.getObject();

            for (Order currentOrder : orders) {
                if (currentOrder.getName().equals(orderToCheck.getName()) && currentOrder.getId().equals(orderToCheck.getId())) {

                    status = currentOrder.getStatus();
                    break;

                }
            }

            Message returnedMessage = new Message("Success", status);

            ous.writeObject(returnedMessage);
            ous.flush();
        }

        /**
         * Order cost
         * Gets the order cost
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void orderCost(Message sent, ObjectOutputStream ous) throws IOException {

            Order orderToCheck = (Order) sent.getObject();
            Integer orderCost = 0;

            for (Order currentOrder : orders) {
                if (currentOrder.getName().equals(orderToCheck.getName()) && (currentOrder.getId().equals(orderToCheck.getId()))) {
                    orderCost = currentOrder.getCost();
                    break;
                }
            }
            Message returnedMessage = new Message("Success", orderCost);

            ous.writeObject(returnedMessage);
            ous.flush();

        }

        /**
         * Cancel Order
         * Cancels the order
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void cancelOrder(Message sent, ObjectOutputStream ous) throws IOException {

            Order orderToCheck = (Order) sent.getObject();

            for (Order currentOrder : orders) {
                if (currentOrder.getName().equals(orderToCheck.getName()) && currentOrder.getId() == orderToCheck.getId()) {

                    orders.remove(currentOrder);

                    Integer id = currentOrder.getId();
                    id--;
                    ordersId.put(currentOrder.getName(), id);
                    break;

                }
            }

            Message returnedMessage = new Message("Success", null);
            ous.writeObject(returnedMessage);
            ous.flush();
        }


        /**
         * Checks if order is complete
         * @param sent
         * @param ous
         * @throws IOException
         */
        private synchronized void isOrderComplete(Message sent, ObjectOutputStream ous) throws IOException {

            Order orderToCheck = (Order) sent.getObject();

            Boolean isComplete = false;

            for (Order currentOrder : orders) {
                if (currentOrder.getName().equals(orderToCheck.getName()) && currentOrder.getId() == orderToCheck.getId()) {

                    isComplete = currentOrder.getOrderComplete();
                    break;

                }
            }

            Message returnedMessage = new Message("Success", isComplete);
            ous.writeObject(returnedMessage);
            ous.flush();
        }

        /**
         * Subtracts the ready dishes from the inventory
         * @param user
         */
        public synchronized void subtract(User user) {
            for (Map.Entry<Dish, Number> entry : user.getBasket().entrySet()) {

                Integer neededDishes = (Integer) entry.getValue();
                Integer currentStock = (Integer) inventory.getDishes().get(entry.getKey());

                Integer sum = currentStock - neededDishes;

                inventory.putDish(entry.getKey(), sum);
            }

        }

    }

}

