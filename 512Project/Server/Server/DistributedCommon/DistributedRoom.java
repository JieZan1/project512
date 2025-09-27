package Server.DistributedCommon;

public class DistributedRoom {
    private String location;
    private Integer rooms;
    private Integer price;
    public DistributedRoom(String location, Integer seats, Integer price) {
        this.location = location;
        this.rooms = seats;
        this.price = price;
    }
    public String getFlight_number() {
        return location;
    }
    public void setFlight_number(String location) {
        this.location = location;
    }
    public Integer getRooms() {
        return rooms;
    }
    public boolean roomExists() {
        return rooms > 0;
    }
    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

}