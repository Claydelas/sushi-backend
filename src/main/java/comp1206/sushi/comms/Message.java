package comp1206.sushi.comms;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int USERS = 0, MESSAGE = 1, LOGOUT = 2, BACKUP = 3;
    private int type;
    private String message;
    private Object response;

    // constructor
    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public Message(Object response){
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
