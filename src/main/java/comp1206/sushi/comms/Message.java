package comp1206.sushi.comms;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int
            MESSAGE = 0, USERS = 1, POSTCODES = 2,
            LOGOUT = 3, BACKUP = 4, RESTAURANT = 5,
            ORDERS = 6, REGISTER = 7, LOGIN = 8,
            DISHES = 9, UPDATE_BASKET = 10, CHECKOUT = 11,
            CLEAR_BASKET = 12, ORDER_STATUS = 13, ORDER_COMPLETE = 14;
    private int type;
    private String message;
    private Object response;

    // constructor
    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public Message(Object response) {
        this.response = response;
    }

    int getType() {
        return type;
    }

    String getMessage() {
        return message;
    }

    public Object getResponse() {
        if (response != null) return response;
        else return "No server response.";
    }
}
