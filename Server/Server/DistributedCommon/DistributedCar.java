package Server.DistributedCommon;

public class DistributedCar {
    private String location;
    private Integer cars;
    private Integer price;
    public DistributedCar(String location, Integer seats, Integer price) {
        this.location = location;
        this.cars = seats;
        this.price = price;
    }
    public String getFlight_number() {
        return location;
    }
    public void setFlight_number(String location) {
        this.location = location;
    }
    public Integer getCars() {
        return cars;
    }
    public boolean carExists() {
        return cars > 0;
    }
    public void setCars(Integer cars) {
        this.cars = cars;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
}
