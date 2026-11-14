package bac.ac.skywings.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Flight {
    private int id;
    private String flightCode;
    private String origin;
    private String destination;
    private Timestamp departureDatetime;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal ticketPrice;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFlightCode() { return flightCode; }
    public void setFlightCode(String flightCode) { this.flightCode = flightCode; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Timestamp getDepartureDatetime() { return departureDatetime; }
    public void setDepartureDatetime(Timestamp departureDatetime) { this.departureDatetime = departureDatetime; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
}