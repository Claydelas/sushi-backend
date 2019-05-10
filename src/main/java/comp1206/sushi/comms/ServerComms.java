package comp1206.sushi.comms;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;
import comp1206.sushi.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class ServerComms {
    // a unique ID for each connection
    private static int uniqueId;
    // an ArrayList to keep the list of the Client
    private ArrayList<ClientThread> al;
    // to display time
    private SimpleDateFormat sdf;
    // the port number to listen for connection
    private int port;
    // to check if server is running
    private boolean keepGoing;
    private Server server;

    //constructor that receive the port to listen to for connection as parameter

    public ServerComms(Server server, int port) {
        this.server = server;
        // the port
        this.port = port;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // an ArrayList to keep the list of the Client
        al = new ArrayList<>();
        new Thread(this::start).start();
    }

    public void start() {
        keepGoing = true;
        //create socket server and wait for connection requests
        try {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);

            // infinite loop to wait for connections ( till server is active )
            while (keepGoing) {
                System.out.println("Server waiting for Clients on port " + port + ".");

                // accept connection if requested from client
                Socket socket = serverSocket.accept();
                // break if server stopped
                if (!keepGoing)
                    break;
                // if client is connected, create its thread
                ClientThread t = new ClientThread(socket);
                //add this client to array list
                al.add(t);
                t.start();
            }
            // try to stop the server
            try {
                serverSocket.close();
                for (ClientThread tc : al) {
                    try {
                        // close all data streams and socket
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ignored) {
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
            System.out.println(sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n");
        }
    }

    // to stop the server
    protected void stop() {
        keepGoing = false;
        try {
            new Socket("localhost", port);
        } catch (Exception ignored) {
        }
    }

    // to broadcast a message to all Clients
    private synchronized boolean broadcast(Object message) {
        for (ClientThread ct : al) {
            ct.writeMsg(new Message(message));
        }
        return true;
    }

    // if client sent LOGOUT message to exit
    private synchronized void remove(int id) {
        al.remove(id);
    }


    // One instance of this thread will run for each client
    class ClientThread extends Thread {
        // the socket to get messages from client
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // my unique id (easier for disconnection)
        int id;
        // message object to receive message and its type
        Message cm;
        //global user
        User user;
        // timestamp
        String date;

        // Constructor
        ClientThread(Socket socket) {
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
            //Creating both Data Stream
            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                broadcast("SUCCESS Client");
            } catch (IOException e) {
                System.out.println("Exception creating new Input/output Streams: " + e);
                return;
            }
            date = new Date().toString() + "\n";
        }

        // infinite loop to read and forward message
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while (keepGoing) {
                // read a String (which is an object)
                try {
                    cm = (Message) sInput.readObject();
                } catch (IOException e) {
                    System.out.println("Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }

                // different actions based on type message
                switch (cm.getType()) {
                    case Message.MESSAGE:
//                        boolean confirmation = broadcast(cm.getMessage());
//                        if (!confirmation) writeMsg(new Message("Sorry. No such user exists."));
                        break;
                    case Message.LOGOUT:
                        System.out.println(id + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case Message.USERS:
                        writeMsg(new Message(server.getUsers()));
                        break;
                    case Message.POSTCODES:
                        writeMsg(new Message(server.getPostcodes()));
                        break;
                    case Message.DISHES:
                        writeMsg(new Message(server.getDishes()));
                        break;
                    case Message.RESTAURANT:
                        writeMsg(new Message(server.getRestaurant()));
                        break;
                    case Message.UPDATE_BASKET:
                        String[] basketDishData = cm.getMessage().split(":");
                        if (user != null) {
                            server.getDishes()
                                    .stream()
                                    .filter(dish -> dish.getName().equals(basketDishData[1]))
                                    .findFirst().ifPresent(dish -> user.addToBasket(dish, Integer.parseInt(basketDishData[2])));
                        } else {
                            server.getUsers()
                                    .stream()
                                    .filter(userInfo -> userInfo.getName().equals(basketDishData[0]))
                                    .findFirst().ifPresent(u -> server.getDishes()
                                    .stream()
                                    .filter(dish -> dish.getName().equals(basketDishData[1]))
                                    .findFirst().ifPresent(dish -> u.addToBasket(dish, Integer.parseInt(basketDishData[2]))));
                        }
                        break;
                    case Message.CHECKOUT:
                        if (user != null) {
                            LinkedHashMap<Dish, Number> items = new LinkedHashMap<>(user.getBasket());
                            Order order = new Order(user, items);
                            server.orders.add(order);
                            server.orderQueue.add(order);
                            user.clearBasket();
                            writeMsg(new Message(order));
                        } else {
                            server.getUsers()
                                    .stream()
                                    .filter(user -> user.getName().equals(cm.getMessage()))
                                    .findFirst().ifPresent(user -> {
                                LinkedHashMap<Dish, Number> items = new LinkedHashMap<>(user.getBasket());
                                Order order = new Order(user, items);
                                server.orders.add(order);
                                server.orderQueue.add(order);
                                user.clearBasket();
                                writeMsg(new Message(order));
                            });
                        }
                        server.notifyUpdate();
                        break;
                    case Message.CLEAR_BASKET:
                        if (user != null) {
                            user.clearBasket();
                        } else {
                            server.getUsers()
                                    .stream()
                                    .filter(user -> user.getName().equals(cm.getMessage()))
                                    .findFirst().ifPresent(User::clearBasket);
                        }
                        break;
                    case Message.ORDERS:
                        writeMsg(new Message(server.getUserOrders(cm.getMessage())));
                        break;
                    case Message.BACKUP:
                        server.notifyUpdate();
                        break;
                    case Message.ORDER_STATUS:
                        server.getOrders()
                                .stream()
                                .filter(order -> order.getName().equals(cm.getMessage()))
                                .findFirst().ifPresent(order -> writeMsg(new Message(order.getStatus())));
                        break;
                    case Message.ORDER_COMPLETE:
                        server.getOrders()
                                .stream()
                                .filter(order -> order.getName().equals(cm.getMessage()))
                                .findFirst().ifPresent(order -> writeMsg(new Message(order.getComplete())));
                        break;
                    case Message.REGISTER:
                        String[] userData = cm.getMessage().split(":");
                        if (server.getUsers().stream().noneMatch(user -> userData[0].equals(user.getName()))) {
                            server.getPostcodes()
                                    .stream()
                                    .filter(postcode -> postcode.getName().equals(userData[3]))
                                    .findFirst().ifPresent(postcode -> {
                                User newUser = new User(userData[0], userData[1], userData[2], postcode);
                                server.users.add(newUser);
                                writeMsg(new Message(newUser));
                                this.user = newUser;
                                server.notifyUpdate();
                            });
                        } else writeMsg(new Message(null));
                        break;
                    case Message.LOGIN:
                        String[] loginInfo = cm.getMessage().split(":");
                        server.getUsers()
                                .stream()
                                .filter(user -> user.getName().equals(loginInfo[0]))
                                .findFirst().ifPresent(user -> {
                            if (user.getPassword().equals(loginInfo[1])) {
                                writeMsg(new Message(user));
                                this.user = user;
                            } else
                                writeMsg(new Message(null));
                        });
                        break;
                }
            }
            // if out of the loop then disconnected and remove from client list
            remove(id - 1);
            close();
        }

        // close everything
        private void close() {
            try {
                System.out.println("Closing all streams...");
                if (sOutput != null) sOutput.close();
                if (sInput != null) sInput.close();
                if (socket != null) socket.close();
            } catch (Exception ignored) {
            }
        }

        // write a String to the Client output stream
        boolean writeMsg(Message msg) {
            // if Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                System.out.println("Error sending message");
            }
            return true;
        }
    }
}