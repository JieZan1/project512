// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package MiddleWare.RMI;

import MiddleWare.Customers.CustomerManager;
import Server.Interface.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;



public class RMIMiddleware implements IResourceManager
{
    private static String s_middleWareName = "MiddleWare"; //Kept fix since not likely to change

    private static String s_rmiPrefix = "group_45_";
    private static int s_middleWarePort = 1045;

    //name of server
    private static String s_server_car = "";
    private static String s_server_room = "";
    private static String s_server_flight = "";
    private static int s_serverPort = 1045;

    private static String s_carRM = "Cars";
    private static String s_roomRM = "Room";
    private static String s_flightRM = "Flights";

    public IResourceManager carRM;
    public IResourceManager roomRM ;
    public IResourceManager flightRM;

    //	private int nextCustomerId = 1000;
    private CustomerManager customerManager;
//  private HashMap<Integer,Customer> customers = new HashMap<>();

    private HashMap<Integer, Integer> m_Flights_available;
    private HashMap<String, Integer> m_Cars_available;
    private HashMap<String, Integer> m_Rooms_available;


    public static void main(String args[])
    {
        if (args.length > 0)
        {
            s_server_flight = args[0];
            s_server_car = args[1];
            s_server_room = args[2];
            System.out.println("DEBUG: Starting middleware with servers - Flight: " + s_server_flight + ", Car: " + s_server_car + ", Room: " + s_server_room);

        }

        // Create the RMI server entry
        try {
            // Create a new Server object
            RMIMiddleware server = new RMIMiddleware();

            //Get the RMs from the RM servers.
            server.flightRM = server.connectServer(s_server_flight, s_serverPort, s_flightRM);
            server.carRM = server.connectServer(s_server_car, s_serverPort, s_carRM);
            server.roomRM = server.connectServer(s_server_room, s_serverPort, s_roomRM);
            System.out.println("Middleware started, connected to all three server.");


            // Dynamically generate the stub (client proxy)
            IResourceManager middleWare = (IResourceManager)UnicastRemoteObject.exportObject(server, 0);

            // Bind the remote object's stub in the registry; adjust port if appropriate
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(s_middleWarePort);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(s_middleWarePort);
            }
            final Registry registry = l_registry;
            registry.rebind(s_rmiPrefix + s_middleWareName, middleWare);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        registry.unbind(s_rmiPrefix + s_middleWareName);
                        System.out.println("'" + s_middleWareName + "' resource manager unbound");
                    }
                    catch(Exception e) {
                        System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("'" + s_middleWareName + "' resource manager server ready and bound to '" + s_rmiPrefix + s_middleWareName + "'");
            System.out.println("DEBUG: Middleware fully initialized and ready for connections");

        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

    }

    public RMIMiddleware (){
        customerManager = CustomerManager.getInstance();
        m_Flights_available = new HashMap<>();
        m_Cars_available = new HashMap<>();
        m_Rooms_available = new HashMap<>();
        System.out.println("DEBUG: RMIMiddleware constructor - initialized data structures");
    }

    /**
     * Add seats to a flight.
     *
     * In general this will be used to create a new
     * flight, but it should be possible to add seats to an existing flight.
     * Adding to an existing flight should overwrite the current price of the
     * available seats.
     *
     * @return Success
     */
    public boolean addFlight(int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        System.out.println("DEBUG: addFlight called - Flight: " + flightNum + ", Seats: " + flightSeats + ", Price: " + flightPrice);
        m_Flights_available.merge(flightNum, flightSeats, Integer::sum);
        boolean result = this.flightRM.addFlight(flightNum, flightSeats, flightPrice);
        System.out.println("DEBUG: addFlight result: " + result + ", Total available seats: " + m_Flights_available.get(flightNum));
        return result;
    }

    /**
     * Add car at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addCars(String location, int numCars, int price) throws RemoteException {
        System.out.println("DEBUG: addCars called - Location: " + location + ", Cars: " + numCars + ", Price: " + price);
        m_Cars_available.merge(location, numCars, Integer::sum);
        boolean result = this.carRM.addCars(location, numCars, price);
        System.out.println("DEBUG: addCars result: " + result + ", Total available cars: " + m_Cars_available.get(location));
        return result;
    }

    /**
     * Add room at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addRooms(String location, int numRooms, int price) throws RemoteException {
        System.out.println("DEBUG: addRooms called - Location: " + location + ", Rooms: " + numRooms + ", Price: " + price);
        m_Rooms_available.merge(location, numRooms, Integer::sum);
        boolean result = this.roomRM.addRooms(location, numRooms, price);
        System.out.println("DEBUG: addRooms result: " + result + ", Total available rooms: " + m_Rooms_available.get(location));
        return result;
    }

    /**
     * Add customer.
     *
     * @return Unique customer identifier
     */
    public int newCustomer() throws RemoteException {
        System.out.println("DEBUG: newCustomer called (auto-generate ID)");
        int customerId = customerManager.newCustomer();
        System.out.println("DEBUG: newCustomer created with ID: " + customerId);
        return customerId;
    }

    /**
     * Add customer with id.
     *
     * @return Success
     */
    public boolean newCustomer(int cid) throws RemoteException {
        System.out.println("DEBUG: newCustomer called with specific ID: " + cid);
        boolean result = customerManager.newCustomer(cid);
        System.out.println("DEBUG: newCustomer with ID " + cid + " result: " + result);
        return result;
    }

    /**
     * Delete the flight.
     *
     * deleteFlight implies whole deletion of the flight. If there is a
     * reservation on the flight, then the flight cannot be deleted
     *
     * @return Success
     */
    public boolean deleteFlight(int flightNum) throws RemoteException {
        System.out.println("DEBUG: deleteFlight called - Flight: " + flightNum);
        if (customerManager.isFlightReserved(flightNum)){
            System.out.println("DEBUG: deleteFlight failed - flight " + flightNum + " has reservations");
            return false;
        }
        boolean result = this.flightRM.deleteFlight(flightNum);
        System.out.println("DEBUG: deleteFlight result: " + result);
        return result;
    }

    /**
     * Delete all cars at a location.
     *
     * It may not succeed if there are reservations for this location
     *
     * @return Success
     */
    public boolean deleteCars(String location) throws RemoteException {
        System.out.println("DEBUG: deleteCars called - Location: " + location);
        if (customerManager.isCarReserved(location)){
            System.out.println("DEBUG: deleteCars failed - location " + location + " has reservations");
            return false;
        }
        boolean result = this.carRM.deleteCars(location);
        System.out.println("DEBUG: deleteCars result: " + result);
        return result;
    }

    /**
     * Delete all rooms at a location.
     *
     * It may not succeed if there are reservations for this location.
     *
     * @return Success
     */
    public boolean deleteRooms(String location) throws RemoteException {
        System.out.println("DEBUG: deleteRooms called - Location: " + location);
        if (customerManager.isRoomReserved(location)){
            System.out.println("DEBUG: deleteRooms failed - location " + location + " has reservations");
            return false;
        }
        boolean result = this.roomRM.deleteRooms(location);
        System.out.println("DEBUG: deleteRooms result: " + result);
        return result;
    }

    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    public boolean deleteCustomer(int customerID) throws RemoteException {
        System.out.println("DEBUG: deleteCustomer called - Customer ID: " + customerID);
        // Get the customer's reserved items
        Map<Integer, Integer>reservedFlights = customerManager.getCustomerFlights(customerID);
        Map<String, Integer> reservedCars = customerManager.getCustomerCars(customerID);
        Map<String, Integer> reservedRooms = customerManager.getCustomerRooms(customerID);

        for (Integer flight_num : reservedFlights.keySet()) {
            m_Flights_available.merge(flight_num, reservedFlights.get(flight_num), Integer::sum);
        }
        for (String location : reservedRooms.keySet()) {
            m_Rooms_available.merge(location, reservedRooms.get(location), Integer::sum);
        }
        for (String location : reservedCars.keySet()) {
            m_Cars_available.merge(location, reservedCars.get(location), Integer::sum);
        }

        boolean result = customerManager.deleteCustomer(customerID);
        System.out.println("DEBUG: deleteCustomer result: " + result + ", Released " + reservedFlights.size() + " flights, " + reservedCars.size() + " cars, " + reservedRooms.size() + " rooms");
        return result;
    }

    /**
     * Query the status of a flight.
     *
     * @return Number of empty seats
     */
    public int queryFlight(int flightNumber) throws RemoteException {
        System.out.println("DEBUG: queryFlight called - Flight: " + flightNumber);
        int seat = 0;
        Integer seats = m_Flights_available.get(flightNumber);
        if (seats != null) {
            seat = seats.intValue();
        }
        System.out.println("DEBUG: queryFlight result: " + seat + " seats available");
        return seat;
    }

    /**
     * Query the status of a car location.
     *
     * @return Number of available cars at this location
     */
    public int queryCars(String location) throws RemoteException {
        System.out.println("DEBUG: queryCars called - Location: " + location);
        int cars = 0;
        Integer seats = m_Cars_available.get(location);
        if (seats != null) {
            cars = seats.intValue();
        }
        System.out.println("DEBUG: queryCars result: " + cars + " cars available");
        return cars;
    }

    /**
     * Query the status of a room location.
     *
     * @return Number of available rooms at this location
     */
    public int queryRooms(String location) throws RemoteException {
        System.out.println("DEBUG: queryRooms called - Location: " + location);
        int rooms = 0;
        Integer seats = m_Rooms_available.get(location);
        if (seats != null) {
            rooms = seats.intValue();
        }
        System.out.println("DEBUG: queryRooms result: " + rooms + " rooms available");
        return rooms;
    }

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    public String queryCustomerInfo(int customerID) throws RemoteException {
        System.out.println("DEBUG: queryCustomerInfo called - Customer ID: " + customerID);
        Map<Integer, Integer> reservedFlights = customerManager.getCustomerFlights(customerID);
        Map<String, Integer> reservedCars = customerManager.getCustomerCars(customerID);
        Map<String, Integer> reservedRooms = customerManager.getCustomerRooms(customerID);

        String s = "Bill for customer " + customerID + "\n";

        // Check if customer exists and has any reservations
        if (reservedFlights.isEmpty() && reservedCars.isEmpty() && reservedRooms.isEmpty()) {
            s += "No reservations found for this customer.\n";
            System.out.println("DEBUG: queryCustomerInfo - No reservations found for customer " + customerID);
            return s;
        }

        // Process flight reservations
        if (!reservedFlights.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : reservedFlights.entrySet()) {
                Integer flightNum = entry.getKey();
                Integer quantity = entry.getValue();
                Integer price = queryFlightPrice(flightNum);

                s += quantity + " flight-" + flightNum + " " + price + "\n";
            }
        }

        // Process car reservations
        if (!reservedCars.isEmpty()) {
            for (Map.Entry<String, Integer> entry : reservedCars.entrySet()) {
                String carLocation = entry.getKey();
                Integer quantity = entry.getValue();
                Integer price = queryCarsPrice(carLocation);

                s += quantity + " car-" + carLocation + " " + price + "\n";
            }
        }

        // Process room reservations
        if (!reservedRooms.isEmpty()) {
            for (Map.Entry<String, Integer> entry : reservedRooms.entrySet()) {
                String roomLocation = entry.getKey();
                Integer quantity = entry.getValue();
                Integer price = queryRoomsPrice(roomLocation);

                s += quantity + " room-" + roomLocation + " " + price + "\n";
            }
        }

        System.out.println("DEBUG: queryCustomerInfo generated bill with " + reservedFlights.size() + " flights, " + reservedCars.size() + " cars, " + reservedRooms.size() + " rooms");
        return s;
    }

//    public String getBill()
//    {
//        String s = "Bill for customer " + m_ID + "\n";
//        for (String key : m_reservations.keySet())
//        {
//            ReservedItem item = (ReservedItem) m_reservations.get(key);
//            s += + item.seat/num() + " " + item.getReservableItemKey() + " $" + item.getPrice() + "\n";
//        }
//        return s;
//    }

    /**
     * Query the status of a flight.
     *
     * @return Price of a seat in this flight
     */
    public int queryFlightPrice(int flightNumber) throws RemoteException {
        System.out.println("DEBUG: queryFlightPrice called - Flight: " + flightNumber);
        int price = this.flightRM.queryFlightPrice(flightNumber);
        System.out.println("DEBUG: queryFlightPrice result: $" + price);
        return price;
    }

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    public int queryCarsPrice(String location) throws RemoteException {
        System.out.println("DEBUG: queryCarsPrice called - Location: " + location);
        int price = this.carRM.queryCarsPrice(location);
        System.out.println("DEBUG: queryCarsPrice result: $" + price);
        return price;
    }

    /**
     * Query the status of a room location.
     *
     * @return Price of a room
     */
    public int queryRoomsPrice(String location) throws RemoteException {
        System.out.println("DEBUG: queryRoomsPrice called - Location: " + location);
        int price = this.roomRM.queryRoomsPrice(location);
        System.out.println("DEBUG: queryRoomsPrice result: $" + price);
        return price;
    }

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    public boolean reserveFlight(int customerID, int flightNumber) throws RemoteException {
        System.out.println("DEBUG: reserveFlight called - Customer: " + customerID + ", Flight: " + flightNumber);
        Integer seat = m_Flights_available.get(flightNumber);

        if (seat != null && seat > 0) {
            m_Flights_available.merge(flightNumber, -1, Integer::sum);
            customerManager.reserveFlight(customerID, flightNumber);
            System.out.println("DEBUG: reserveFlight successful, remaining seats: " + m_Flights_available.get(flightNumber));
            return true;
        }
        else {
            System.out.println("DEBUG: reserveFlight failed - insufficient seats available: " + (seat != null ? seat : "null"));
            return false;
        }
    }

    /**
     * Reserve a car at this location.
     *
     * @return Success
     */
    public boolean reserveCar(int customerID, String location) throws RemoteException {
        System.out.println("DEBUG: reserveCar called - Customer: " + customerID + ", Location: " + location);
        Integer seat = m_Cars_available.get(location);

        if (seat != null && seat > 0) {
            m_Cars_available.merge(location, -1, Integer::sum);
            customerManager.reserveCar(customerID, location);
            System.out.println("DEBUG: reserveCar successful, remaining cars: " + m_Cars_available.get(location));
            return true;
        }
        else {
            System.out.println("DEBUG: reserveCar failed - insufficient cars available: " + (seat != null ? seat : "null"));
            return false;
        }
    }

    /**
     * Reserve a room at this location.
     *
     * @return Success
     */
    public boolean reserveRoom(int customerID, String location) throws RemoteException {
        System.out.println("DEBUG: reserveRoom called - Customer: " + customerID + ", Location: " + location);
        Integer seat = m_Rooms_available.get(location);

        if (seat != null && seat > 0) {
            m_Rooms_available.merge(location, -1, Integer::sum);
            customerManager.reserveRoom(customerID, location);
            System.out.println("DEBUG: reserveRoom successful, remaining rooms: " + m_Rooms_available.get(location));
            return true;
        }
        else {
            System.out.println("DEBUG: reserveRoom failed - insufficient rooms available: " + (seat != null ? seat : "null"));
            return false;
        }
    }

    /**
     * Reserve a bundle for the trip.
     *
     * @return Success
     */
    public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        System.out.println("DEBUG: bundle called - Customer: " + customerID + ", Flights: " + flightNumbers + ", Location: " + location + ", Car: " + car + ", Room: " + room);
        for (String flightNumber : flightNumbers) {
            int flightNumberInt = Integer.parseInt(flightNumber);
            if (m_Flights_available.containsKey(flightNumber) && m_Flights_available.get(flightNumber) > 1) {
                customerManager.reserveFlight(customerID,flightNumberInt);
                m_Flights_available.merge(flightNumberInt, -1, Integer::sum);
            }
            else{
                System.out.println("DEBUG: bundle failed - flight " + flightNumber + " not available");
                return false;
            }
        }
        if (car) {
            if (m_Cars_available.containsKey(location) && m_Cars_available.get(location) > 1) {
                customerManager.reserveCar(customerID,location);
                m_Cars_available.merge(location, -1, Integer::sum);
            }
            else{
                System.out.println("DEBUG: bundle failed - car at " + location + " not available");
                return false;
            }
        }
        if (room) {
            if (m_Rooms_available.containsKey(location) && m_Rooms_available.get(location) > 1) {
                customerManager.reserveRoom(customerID,location);
                m_Rooms_available.merge(location, -1, Integer::sum);
            }
            else {
                System.out.println("DEBUG: bundle failed - room at " + location + " not available");
                return false;
            }
        }
        System.out.println("DEBUG: bundle successful for customer " + customerID);
        return true;
    }

    /**
     * Convenience for probing the resource manager.
     *
     * @return Name
     */
    public String getName() throws RemoteException {
        return s_middleWareName;
    }

    public String removeBillHeader(String bill) {
        if (bill == null || bill.isEmpty()) {
            return "";
        }
        int idx = bill.indexOf("\n");
        if (idx == -1) {
            return ""; // no newline means no content after header
        }
        return bill.substring(idx + 1).trim(); // remove header and trim whitespace
    }

    public IResourceManager connectServer(String server, int port, String name)
    {
        try {
            IResourceManager output;
            boolean first = true;
            while (true) {
                try {
                    Registry registry = LocateRegistry.getRegistry(server, port);
                    output = (IResourceManager)registry.lookup(s_rmiPrefix + name);
                    System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
                    return output;
                }
                catch (NotBoundException|RemoteException e) {
                    if (first) {
                        System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
                        first = false;
                    }
                }
                Thread.sleep(500);
            }

        }
        catch (Exception e) {
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

}