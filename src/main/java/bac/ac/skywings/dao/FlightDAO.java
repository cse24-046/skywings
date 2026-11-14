package bac.ac.skywings.dao;

import bac.ac.skywings.model.Flight;
import bac.ac.skywings.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public List<Flight> searchFlights(String origin, String destination) {
        List<Flight> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM flights WHERE departure_datetime > NOW()");

        boolean hasOrigin = (origin != null && !origin.trim().isEmpty() && !origin.equals("ALL"));
        boolean hasDest = (destination != null && !destination.trim().isEmpty() && !destination.equals("ALL"));

        if (hasOrigin) sql.append(" AND origin LIKE ?");
        if (hasDest) sql.append(" AND destination LIKE ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasOrigin) stmt.setString(idx++, "%" + origin.trim() + "%");
            if (hasDest) stmt.setString(idx++, "%" + destination.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Flight f = new Flight();
                    f.setId(rs.getInt("id"));
                    f.setFlightCode(rs.getString("flight_code"));
                    f.setOrigin(rs.getString("origin"));
                    f.setDestination(rs.getString("destination"));
                    f.setDepartureDatetime(rs.getTimestamp("departure_datetime"));
                    f.setTotalSeats(rs.getInt("total_seats"));
                    f.setAvailableSeats(rs.getInt("available_seats"));
                    f.setTicketPrice(rs.getBigDecimal("ticket_price"));
                    list.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createFlight(Flight f) {
        String sql = "INSERT INTO flights (flight_code, origin, destination, departure_datetime, total_seats, available_seats, ticket_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getFlightCode());
            stmt.setString(2, f.getOrigin());
            stmt.setString(3, f.getDestination());
            stmt.setTimestamp(4, f.getDepartureDatetime());
            stmt.setInt(5, f.getTotalSeats());
            stmt.setInt(6, f.getTotalSeats()); // Initial available equals total
            stmt.setBigDecimal(7, f.getTicketPrice());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFlight(int flightId) {
        String sql = "DELETE FROM flights WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}