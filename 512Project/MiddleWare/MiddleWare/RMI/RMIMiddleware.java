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
        m_Flights_available.merge(flightNum, flightSeats, Integer::sum);
		return this.flightRM.addFlight(flightNum, flightSeats, flightPrice);
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
        m_Cars_available.merge(location, numCars, Integer::sum);
		return this.carRM.addCars(location, numCars, price);
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
        m_Rooms_available.merge(location, numRooms, Integer::sum);
		return this.roomRM.addRooms(location, numRooms, price);
	}
			    
    /**
     * Add customer.
     *
     * @return Unique customer identifier
     */
    public int newCustomer() throws RemoteException {
        return customerManager.newCustomer();
	}
    
    /**
     * Add customer with id.
     *
     * @return Success
     */
    public boolean newCustomer(int cid) throws RemoteException {
        return customerManager.newCustomer(cid);
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
        if (customerManager.isFlightReserved(flightNum)){
                return false;
        }
        return this.flightRM.deleteFlight(flightNum);
    }
    
    /**
     * Delete all cars at a location.
     *
     * It may not succeed if there are reservations for this location
     *
     * @return Success
     */		    
    public boolean deleteCars(String location) throws RemoteException {
        if (customerManager.isCarReserved(location)){
            return false;
        }
        return this.carRM.deleteCars(location);
    }

    /**
     * Delete all rooms at a location.
     *
     * It may not succeed if there are reservations for this location.
     *
     * @return Success
     */
    public boolean deleteRooms(String location) throws RemoteException {
        if (customerManager.isRoomReserved(location)){
            return false;
        }
        return this.roomRM.deleteRooms(location);
    }
    
    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    public boolean deleteCustomer(int customerID) throws RemoteException {
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

        return customerManager.deleteCustomer(customerID);
    }

    /**
     * Query the status of a flight.
     *
     * @return Number of empty seats
     */
    public int queryFlight(int flightNumber) throws RemoteException {
        int seat = 0;
        Integer seats = m_Flights_available.get(flightNumber);
        if (seats != null) {
            seat = seats.intValue();
        }
	    return seat;
	}

    /**
     * Query the status of a car location.
     *
     * @return Number of available cars at this location
     */
    public int queryCars(String location) throws RemoteException {
        int cars = 0;
        Integer seats = m_Cars_available.get(location);
        if (seats != null) {
            cars = seats.intValue();
        }
        return cars;
	  }

    /**
     * Query the status of a room location.
     *
     * @return Number of available rooms at this location
     */
    public int queryRooms(String location) throws RemoteException {
        int rooms = 0;
        Integer seats = m_Rooms_available.get(location);
        if (seats != null) {
            rooms = seats.intValue();
        }
        return rooms;
	}

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    public String queryCustomerInfo(int customerID) throws RemoteException {
        Map<Integer, Integer> reservedFlights = customerManager.getCustomerFlights(customerID);
        Map<String, Integer> reservedCars = customerManager.getCustomerCars(customerID);
        Map<String, Integer> reservedRooms = customerManager.getCustomerRooms(customerID);

        String s = "Bill for customer " + customerID + "\n";

        // Check if customer exists and has any reservations
        if (reservedFlights.isEmpty() && reservedCars.isEmpty() && reservedRooms.isEmpty()) {
            s += "No reservations found for this customer.\n";
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
		return this.flightRM.queryFlightPrice(flightNumber);
	  }

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    public int queryCarsPrice(String location) throws RemoteException {
        return this.carRM.queryCarsPrice(location);
    }

    /**
     * Query the status of a room location.
     *
     * @return Price of a room
     */
    public int queryRoomsPrice(String location) throws RemoteException {
        return this.roomRM.queryRoomsPrice(location);
    }

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    public boolean reserveFlight(int customerID, int flightNumber) throws RemoteException {
        Integer seat = m_Flights_available.get(flightNumber);

        if (seat != null && seat > 1) {
            m_Flights_available.merge(flightNumber, -1, Integer::sum);
            customerManager.reserveFlight(customerID, flightNumber);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Reserve a car at this location.
     *
     * @return Success
     */
    public boolean reserveCar(int customerID, String location) throws RemoteException {
        Integer seat = m_Cars_available.get(location);

        if (seat != null && seat > 1) {
            m_Cars_available.merge(location, -1, Integer::sum);
            customerManager.reserveCar(customerID, location);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Reserve a room at this location.
     *
     * @return Success
     */
    public boolean reserveRoom(int customerID, String location) throws RemoteException {
        Integer seat = m_Rooms_available.get(location);

        if (seat != null && seat > 1) {
            m_Rooms_available.merge(location, -1, Integer::sum);
            customerManager.reserveRoom(customerID, location);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Reserve a bundle for the trip.
     *
     * @return Success
     */
    public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        for (String flightNumber : flightNumbers) {
            int flightNumberInt = Integer.parseInt(flightNumber);
            if (m_Flights_available.containsKey(flightNumber) && m_Flights_available.get(flightNumber) > 1) {
                customerManager.reserveFlight(customerID,flightNumberInt);
                m_Flights_available.merge(flightNumberInt, -1, Integer::sum);
            }
            else{
                return false;
            }
        }
        if (car) {
            if (m_Cars_available.containsKey(location) && m_Cars_available.get(location) > 1) {
                customerManager.reserveCar(customerID,location);
                m_Cars_available.merge(location, -1, Integer::sum);
            }
            else{
                return false;
            }
        }
        if (room) {
            if (m_Rooms_available.containsKey(location) && m_Rooms_available.get(location) > 1) {
                customerManager.reserveRoom(customerID,location);
                m_Rooms_available.merge(location, -1, Integer::sum);
            }
            else {
                return false;
            }
        }
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
