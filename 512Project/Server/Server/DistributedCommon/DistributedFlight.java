package Server.DistributedCommon;

public class DistributedFlight {
    private Integer flight_number;
    private Integer seats;
    private Integer price;
    public DistributedFlight(Integer flight_number, Integer seats, Integer price) {
        this.flight_number = flight_number;
        this.seats = seats;
        this.price = price;
    }
    public Integer getFlight_number() {
        return flight_number;
    }
    public void setFlight_number(Integer flight_number) {
        this.flight_number = flight_number;
    }
    public Integer getSeats() {
        return seats;
    }
    public boolean seatExists() {
        return seats > 0;
    }
    public void setSeats(Integer seats) {
        this.seats = seats;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

}
