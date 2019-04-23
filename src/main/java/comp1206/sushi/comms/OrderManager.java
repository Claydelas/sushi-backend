package comp1206.sushi.comms;


import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Order;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Order manager
 * Checks for not ready Orders
 */
public class OrderManager extends Thread {

    private ArrayList<Order> orders;
    private ArrayList<Drone> drones;

    private CopyOnWriteArrayList<Order> ordersArray;
    private CopyOnWriteArrayList<Drone> dronesArray;

    public OrderManager(ArrayList<Order> orders, ArrayList<Drone> drones) {
        this.orders = orders;
        this.drones = drones;

    }


    /**
     * While the order manager is running
     * Checks for NOT READY ORDERS
     * If there are any not ready
     * It delivers them
     */
    @Override
    public void run() {
        while (true) {

            ordersArray = new CopyOnWriteArrayList<Order>(orders);
            Iterator<Order> it = ordersArray.iterator();

            while (it.hasNext()) {
                Order d = it.next();
                if (d.getStatus().equals("Pending")) {
                    try {
                        sendDrone(d);
                        Thread.sleep(200);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Send the drone to the order to deliver
     */
    public synchronized void sendDrone(Order orderToComplete) throws InterruptedException {
        for (Drone drone : drones) {
            Drone currentDrone = drone;
            if (currentDrone.getStatus().equals("Idle")) {
                //TODO currentDrone.setDeliverOrder(orderToComplete);
                break;

            }
        }

    }
}

