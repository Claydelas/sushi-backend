package comp1206.sushi.comms;

import java.io.Serializable;

/**
 * UserLogin Class
 * Used to send a user that wants to log in through the Message object
 *
 */
public class UserLogin implements Serializable{

    /**
     * Member variables
     */
    private String name;
    private String password;

    /**
     * Constructor
     * @param name
     * @param password
     */
    public UserLogin(String name, String password){
        this.name=name;
        this.password=password;
    }


    /**
     * Getters
     * @return
     */
    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
