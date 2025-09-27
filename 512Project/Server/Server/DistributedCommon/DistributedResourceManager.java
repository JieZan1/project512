package Server.DistributedCommon;

import Server.Common.*;
import Server.Interface.IResourceManager;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;



public class DistributedResourceManager implements IResourceManager {
    protected String m_name = "";

    protected HashMap<Integer, DistributedFlight>  m_flights;
    protected HashMap<String, DistributedRoom>  m_rooms;
    protected HashMap<String, DistributedCar>  m_car;

    public DistributedResourceManager(String p_name)
    {
        m_name = p_name;
    }

    // Reads a data item

    // Create a new flight, or add seats to existing flight
    // NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
    public boolean addFlight(int flightNum, int flightSeats, int flightPrice) throws RemoteException
    {
        Trace.info("RM::addFlight(" + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
        DistributedFlight curObj = m_flights.get(flightNum);
        if (curObj == null)
        {
            // Doesn't exist yet, add it
            DistributedFlight newObj = new DistributedFlight(flightNum, flightSeats, flightPrice);
            m_flights.put(flightNum, newObj);
            Trace.info("RM::addFlight() created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
        }
        else
        {
            // Add seats to existing flight and update the price if greater than zero
            curObj.setSeats(curObj.getSeats() + flightSeats);
            if (flightPrice > 0)
            {
                curObj.setPrice(flightPrice);
            }
            m_flights.put(flightNum, curObj);
            Trace.info("RM::addFlight() modified existing flight " + flightNum + ", seats=" + curObj.getSeats() + ", price=$" + flightPrice);
        }
        return true;
    }

    // Create a new car location or add cars to an existing location
    // NOTE: if price <= 0 and the location already exists, it maintains its current price
    public boolean addCars(String location, int numCars, int price) throws RemoteException {
        Trace.info("RM::addCars(" + location + ", " + numCars + ", $" + price + ") called");
        DistributedCar curObj = m_car.get(location);
        if (curObj == null) {
            // Doesn't exist yet, add it
            DistributedCar newObj = new DistributedCar(location, numCars, price);
            m_car.put(location, newObj);
            Trace.info("RM::addCars() created new cars at " + location + ", count=" + numCars + ", price=$" + price);
        } else {
            // Add cars to existing location and update the price if greater than zero
            curObj.setCars(curObj.getCars() + numCars);
            if (price > 0) {
                curObj.setPrice(price);
            }
            m_car.put(location, curObj);
            Trace.info("RM::addCars() modified cars at " + location + ", count=" + curObj.getCars() + ", price=$" + price);
        }
        return true;
    }


    // Create a new room location or add rooms to an existing location
    // NOTE: if price <= 0 and the room location already exists, it maintains its current price
    public boolean addRooms(String location, int numRooms, int price) throws RemoteException {
        Trace.info("RM::addRooms(" + location + ", " + numRooms + ", $" + price + ") called");
        DistributedRoom curObj = m_rooms.get(location);
        if (curObj == null) {
            // Doesn't exist yet, add it
            DistributedRoom newObj = new DistributedRoom(location, numRooms, price);
            m_rooms.put(location, newObj);
            Trace.info("RM::addRooms() created new rooms at " + location + ", count=" + numRooms + ", price=$" + price);
        } else {
            // Add rooms to existing location and update the price if greater than zero
            curObj.setRooms(curObj.getRooms() + numRooms);
            if (price > 0) {
                curObj.setPrice(price);
            }
            m_rooms.put(location, curObj);
            Trace.info("RM::addRooms() modified rooms at " + location + ", count=" + curObj.getRooms() + ", price=$" + price);
        }
        return true;
    }


    // Deletes flight
    public boolean deleteFlight(int flightNum) throws RemoteException
    {
        if (m_flights.containsKey(flightNum)){
           m_flights.remove(flightNum);
           return true;
        }
        else{
            return false;
        }
    }

    // Delete cars at a location
    public boolean deleteCars(String location) throws RemoteException
    {
        if (m_car.containsKey(location)){
            m_car.remove(location);
            return true;
        }
        else{
            return false;
        }
    }

    // Delete rooms at a location
    public boolean deleteRooms(String location) throws RemoteException
    {
        if (m_rooms.containsKey(location)){
            m_rooms.remove(location);
            return true;
        }
        else{
            return false;
        }
    }

    // Returns the number of empty seats in this flight
    // Returns the number of seats in the flight
    public int queryFlight(int flightNum) throws RemoteException {
        int value = 0;
        DistributedFlight curObj = m_flights.get(flightNum);
        if (curObj != null) {
            value = curObj.getSeats();
        }
        return value;
    }

    // Returns the number of cars available at a location
    public int queryCars(String location) throws RemoteException {
        int value = 0;
        DistributedCar curObj = m_car.get(location);
        if (curObj != null) {
            value = curObj.getCars();
        }
        return value;
    }

    // Returns the number of rooms available at a location
    public int queryRooms(String location) throws RemoteException {
        int value = 0;
        DistributedRoom curObj = m_rooms.get(location);
        if (curObj != null) {
            value = curObj.getRooms();
        }
        return value;
    }

    // Returns price of a seat in this flight
    public int queryFlightPrice(int flightNum) throws RemoteException {
        int price = 0;
        DistributedFlight curObj = m_flights.get(flightNum);
        if (curObj != null) {
            price = curObj.getPrice();
        }
        return price;
    }

    // Returns price of cars at this location
    public int queryCarsPrice(String location) throws RemoteException {
        int price = 0;
        DistributedCar curObj = m_car.get(location);
        if (curObj != null) {
            price = curObj.getPrice();
        }
        return price;
    }

    // Returns room price at this location
    public int queryRoomsPrice(String location) throws RemoteException {
        int price = 0;
        DistributedRoom curObj = m_rooms.get(location);
        if (curObj != null) {
            price = curObj.getPrice();
        }
        return price;
    }

    public String queryCustomerInfo(int customerID) throws RemoteException
    {
        return "";
    }

    public int newCustomer() throws RemoteException
    {
        return -1;
    }

    public boolean newCustomer(int customerID) throws RemoteException
    {
        return false;
    }

    public boolean deleteCustomer(int customerID) throws RemoteException
    {
        return false;
    }

    // Adds flight reservation to this customer
    public boolean reserveFlight(int customerID, int flightNum) throws RemoteException
    {
        return false;
    }

    // Adds car reservation to this customer
    public boolean reserveCar(int customerID, String location) throws RemoteException
    {
        return false;
    }

    // Adds room reservation to this customer
    public boolean reserveRoom(int customerID, String location) throws RemoteException
    {
        return false;
    }

    // Reserve bundle
    public boolean bundle(int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException
    {
        return false;
    }

    public String getName() throws RemoteException
    {
        return m_name;
    }
}
