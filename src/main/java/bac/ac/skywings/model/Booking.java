package bac.ac.skywings.model;

public class Booking {
    private int id;
    private int userId;
    private int flightId;
    private int seatsBooked;
    private double totalAmount;
    private String bookingStatus;

    // Additional fields for joined flight details (used in my-bookings view)
    private String flightCode;
    private String origin;
    private String destination;
    private String departureDatetime;

    // Constructors
    public Booking() {}

    public Booking(int id, int userId, int flightId, int seatsBooked, double totalAmount, String bookingStatus) {
        this.id = id;
        this.userId = userId;
        this.flightId = flightId;
        this.seatsBooked = seatsBooked;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(String departureDatetime) {
        this.departureDatetime = departureDatetime;
    }
}