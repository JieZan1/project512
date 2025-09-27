package MiddleWare.Common;

import java.util.*;

public class Customer {
    private int cid;
    private List<HashMap<Integer, Integer>> flights = new ArrayList<>();
    private List<HashMap<String, Integer>> cars = new ArrayList<>();
    private List<HashMap<String, Integer>> rooms = new ArrayList<>();

    public Customer(int id) {
        cid = id;
        // Initialize with empty HashMaps
        flights.add(new HashMap<>());
        cars.add(new HashMap<>());
        rooms.add(new HashMap<>());
    }

//    /**
//     * Get all the reserved flights of the customer
//     *
//     * @return The list of the flight numbers of the reserved flights
//     */
//    public List<Integer> getFlights() {
//        List<Integer> flightList = new ArrayList<>();
//        for (HashMap<Integer, Integer> flightMap : flights) {
//            for (Map.Entry<Integer, Integer> entry : flightMap.entrySet()) {
//                Integer flightNum = entry.getKey();
//                Integer quantity = entry.getValue();
//                for (int i = 0; i < quantity; i++) {
//                    flightList.add(flightNum);
//                }
//            }
//        }
//        return Collections.unmodifiableList(flightList);
//    }
//
//    /**
//     * Get all the reserved cars of the customer
//     *
//     * @return The list of the locations of the reserved cars
//     */
//    public List<String> getCars() {
//        List<String> carList = new ArrayList<>();
//        for (HashMap<String, Integer> carMap : cars) {
//            for (Map.Entry<String, Integer> entry : carMap.entrySet()) {
//                String location = entry.getKey();
//                Integer quantity = entry.getValue();
//                for (int i = 0; i < quantity; i++) {
//                    carList.add(location);
//                }
//            }
//        }
//        return Collections.unmodifiableList(carList);
//    }
//
//    /**
//     * Get all the reserved rooms of the customer
//     *
//     * @return The list of the locations of the reserved rooms
//     */
//    public List<String> getRooms() {
//        List<String> roomList = new ArrayList<>();
//        for (HashMap<String, Integer> roomMap : rooms) {
//            for (Map.Entry<String, Integer> entry : roomMap.entrySet()) {
//                String location = entry.getKey();
//                Integer quantity = entry.getValue();
//                for (int i = 0; i < quantity; i++) {
//                    roomList.add(location);
//                }
//            }
//        }
//        return Collections.unmodifiableList(roomList);
//    }

    /**
     * Add a reserved flight for the customer.
     *
     * @param flight The flight number to add
     * @return Success
     */
    public boolean addFlight(int flight) {
        if (!flights.isEmpty()) {
            HashMap<Integer, Integer> flightMap = flights.get(0);
            flightMap.put(flight, flightMap.getOrDefault(flight, 0) + 1);
            return true;
        }
        return false;
    }

    /**
     * Add a reserved car for the customer.
     *
     * @param car The car location to add
     * @return Success
     */
    public boolean addCar(String car) {
        if (!cars.isEmpty()) {
            HashMap<String, Integer> carMap = cars.get(0);
            carMap.put(car, carMap.getOrDefault(car, 0) + 1);
            return true;
        }
        return false;
    }

    /**
     * Add a reserved room for the customer.
     *
     * @param room The room location to add
     * @return Success
     */
    public boolean addRoom(String room) {
        if (!rooms.isEmpty()) {
            HashMap<String, Integer> roomMap = rooms.get(0);
            roomMap.put(room, roomMap.getOrDefault(room, 0) + 1);
            return true;
        }
        return false;
    }

    /**
     * Remove all the reserved flights for the customer.
     *
     * @param flight The flight number to remove
     * @return The number of flights removed
     */
    public int removeFlight(int flight) {
        int totalRemoved = 0;
        for (HashMap<Integer, Integer> flightMap : flights) {
            if (flightMap.containsKey(flight)) {
                totalRemoved += flightMap.get(flight);
                flightMap.remove(flight);
            }
        }
        if (totalRemoved > 0) {
            System.out.println("Removed " + totalRemoved + " flight(s) with number " + flight);
        }
        return totalRemoved;
    }

    /**
     * Remove all the reserved cars for the customer.
     *
     * @param car The car location to remove
     * @return The number of cars removed
     */
    public int removeCar(String car) {
        int totalRemoved = 0;
        for (HashMap<String, Integer> carMap : cars) {
            if (carMap.containsKey(car)) {
                totalRemoved += carMap.get(car);
                carMap.remove(car);
            }
        }
        if (totalRemoved > 0) {
            System.out.println("Removed " + totalRemoved + " car(s) at location " + car);
        }
        return totalRemoved;
    }

    /**
     * Remove all the reserved rooms for the customer.
     *
     * @param room The room location to remove
     * @return The number of rooms removed
     */
    public int removeRoom(String room) {
        int totalRemoved = 0;
        for (HashMap<String, Integer> roomMap : rooms) {
            if (roomMap.containsKey(room)) {
                totalRemoved += roomMap.get(room);
                roomMap.remove(room);
            }
        }
        if (totalRemoved > 0) {
            System.out.println("Removed " + totalRemoved + " room(s) at location " + room);
        }
        return totalRemoved;
    }

    /**
     * Get the count of a specific flight reservation for the customer.
     *
     * @param flight The flight number to count
     * @return The number of times this flight is reserved by the customer
     */
    public int getFlightCount(int flight) {
        int totalCount = 0;
        for (HashMap<Integer, Integer> flightMap : flights) {
            totalCount += flightMap.getOrDefault(flight, 0);
        }
        return totalCount;
    }

    /**
     * Get the count of a specific car reservation for the customer.
     *
     * @param car The car location to count
     * @return The number of times this car location is reserved by the customer
     */
    public int getCarCount(String car) {
        int totalCount = 0;
        for (HashMap<String, Integer> carMap : cars) {
            totalCount += carMap.getOrDefault(car, 0);
        }
        return totalCount;
    }

    /**
     * Get the count of a specific room reservation for the customer.
     *
     * @param room The room location to count
     * @return The number of times this room location is reserved by the customer
     */
    public int getRoomCount(String room) {
        int totalCount = 0;
        for (HashMap<String, Integer> roomMap : rooms) {
            totalCount += roomMap.getOrDefault(room, 0);
        }
        return totalCount;
    }

    /**
     * Get the customer ID.
     *
     * @return The customer ID
     */
    public int getCustomerId() {
        return cid;
    }

    /**
     * Get all unique flight numbers with their quantities.
     *
     * @return Map of flight number to total quantity
     */
    public Map<Integer, Integer> getFlightReservations() {
        Map<Integer, Integer> allFlights = new HashMap<>();
        for (HashMap<Integer, Integer> flightMap : flights) {
            for (Map.Entry<Integer, Integer> entry : flightMap.entrySet()) {
                allFlights.put(entry.getKey(),
                        allFlights.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return allFlights;
    }

    /**
     * Get all unique car locations with their quantities.
     *
     * @return Map of car location to total quantity
     */
    public Map<String, Integer> getCarReservations() {
        Map<String, Integer> allCars = new HashMap<>();
        for (HashMap<String, Integer> carMap : cars) {
            for (Map.Entry<String, Integer> entry : carMap.entrySet()) {
                allCars.put(entry.getKey(),
                        allCars.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return allCars;
    }

    /**
     * Get all unique room locations with their quantities.
     *
     * @return Map of room location to total quantity
     */
    public Map<String, Integer> getRoomReservations() {
        Map<String, Integer> allRooms = new HashMap<>();
        for (HashMap<String, Integer> roomMap : rooms) {
            for (Map.Entry<String, Integer> entry : roomMap.entrySet()) {
                allRooms.put(entry.getKey(),
                        allRooms.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return allRooms;
    }
}