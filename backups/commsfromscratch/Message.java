package comp1206.sushi.comms;

import java.io.Serializable;

/**
 * Message Class
 * Implements Serializable
 */
public class Message implements Serializable {


    /**
     * Member variables
     */
    private String command;
    private Object object;
    private Object secondObject;
    private Object thirdObject;


    /**
     * Constructors
     *
     * @param command
     * @param object
     */
    public Message(String command, Object object) {
        this.command = command;
        this.object = object;
    }

    public Message(String command) {
        this.command = command;
    }

    public Message(String command, Object object, Object secondObject) {
        this.command = command;
        this.object = object;
        this.secondObject = secondObject;
    }

    public Message(String command, Object object, Object secondObject, Object thirdObject) {
        this.command = command;
        this.object = object;
        this.secondObject = secondObject;
        this.thirdObject = thirdObject;
    }

    /**
     * Getters
     *
     * @return
     */
    public Object getObject() {
        return object;
    }

    /**
     * Setters
     *
     * @param object
     */

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getSecondObject() {
        return secondObject;
    }

    public Object getThirdObject() {
        return thirdObject;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
