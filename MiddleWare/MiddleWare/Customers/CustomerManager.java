package MiddleWare.Customers;

import MiddleWare.Common.Customer;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for managing customers in a distributed system
 */
public class CustomerManager {

    // Singleton instance
    private static volatile CustomerManager instance;

    // Customer management fields
    private static int nextCustomerId = 1000;
    private HashMap<Integer, Customer> customers;

    // Private constructor to prevent direct instantiation
    private CustomerManager() {
        this.customers = new HashMap<>();
    }

    /**
     * Gets the singleton instance of CustomerManager
     * Thread-safe implementation using double-checked locking
     *
     * @return The singleton instance
     */
    public static CustomerManager getInstance() {
        if (instance == null) {
            synchronized (CustomerManager.class) {
                if (instance == null) {
                    instance = new CustomerManager();
                }
            }
        }
        return instance;
    }

    /**
     * Creates a new customer and save it in the record.
     *
     * @return Unique customer identifier
     */
    public synchronized int newCustomer() {
        while (customers.containsKey(nextCustomerId)) {
            nextCustomerId++;
        }
        customers.put(nextCustomerId, new Customer(nextCustomerId));
        return nextCustomerId++;
    }

    /**
     * Creates a new customer with specified id and save it in the record
     *
     * @param cid The customer ID to create
     * @return Success status
     */
    public synchronized boolean newCustomer(int cid) {
        if (customers.containsKey(cid)) {
            return false;
        } else {
            customers.put(cid, new Customer(cid));
        }
        return true;
    }

    public boolean isFlightReserved(int flightNum) {
        for (Customer customer : customers.values()) {
            if (customer.getFlightCount(flightNum)>0) {
                return true;
            }
        }
        return false;
    }

    public boolean isCarReserved(String location) {
        for (Customer customer : customers.values()) {
            if (customer.getCarCount(location)>0) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoomReserved(String location) {
        for (Customer customer : customers.values()) {
            if (customer.getRoomCount(location)>0) {
                return true;
            }
        }
        return false;
    }

    public Map<Integer, Integer> getCustomerFlights(int customerID) {
        Customer customer = customers.get(customerID);
        if (customer != null) {
            return customer.getFlightReservations();
        }
        return new HashMap<>();
    }

    public Map<String, Integer> getCustomerCars(int customerID) {
        Customer customer = customers.get(customerID);
        if (customer != null) {
            return customer.getCarReservations();
        }
        return new HashMap<>();
    }

    public Map<String, Integer> getCustomerRooms(int customerID) {
        Customer customer = customers.get(customerID);
        if (customer != null) {
            return customer.getRoomReservations();
        }
        return new HashMap<>();
    }

    public boolean deleteCustomer(int customerID) {
        Customer customer = customers.get(customerID);
        if (customer != null) {
            customers.remove(customerID);
            return true;
        }
        return false;
    }

    public boolean reserveFlight(int customerID, int flightNum) {
        Customer customer = customers.get(customerID);
        if (customer != null) {
            customer.addFlight(flightNum);
            return true;
        }
        return false;
    }
    public boolean reserveCar(int customerID, String location) {
        Customer customer = customers.get(customerID);
        if (customer != null) {
            customer.addCar(location);
            return true;
        }
        return false;
    }
    public boolean reserveRoom(int customerID, String location) {
        Customer customer = customers.get(customerID);
        if (customer != null) {
            customer.addRoom(location);
            return true;
        }
        return false;
    }

    public Customer getCustomer(int customerID) {
        return customers.get(customerID);
    }
    public HashMap<Integer, Customer> getAllCustomers() {
        return new HashMap<>(customers);
    }

}