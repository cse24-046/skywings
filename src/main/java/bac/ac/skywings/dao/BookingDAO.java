package bac.ac.skywings.dao;

import bac.ac.skywings.model.Booking;
import bac.ac.skywings.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean bookFlight(int userId, int flightId, int seatsRequested) {
        String checkSeatsSQL = "SELECT available_seats, ticket_price FROM flights WHERE id = ? FOR UPDATE";
        String updateSeatsSQL = "UPDATE flights SET available_seats = available_seats - ? WHERE id = ?";
        String insertBookingSQL = "INSERT INTO bookings (user_id, flight_id, seats_booked, total_amount, booking_status) VALUES (?, ?, ?, ?, 'CONFIRMED')";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Transaction start

            int availableSeats = 0;
            double pricePerSeat = 0.0;

            try (PreparedStatement stmt = conn.prepareStatement(checkSeatsSQL)) {
                stmt.setInt(1, flightId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        availableSeats = rs.getInt("available_seats");
                        pricePerSeat = rs.getDouble("ticket_price");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (availableSeats < seatsRequested) {
                conn.rollback();
                return false; // Not enough seats
            }

            double totalAmount = pricePerSeat * seatsRequested;

            try (PreparedStatement stmt = conn.prepareStatement(updateSeatsSQL)) {
                stmt.setInt(1, seatsRequested);
                stmt.setInt(2, flightId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertBookingSQL)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, flightId);
                stmt.setInt(3, seatsRequested);
                stmt.setDouble(4, totalAmount);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean cancelBooking(int bookingId, int userId) {
        String getBookingSQL = "SELECT b.seats_booked, b.booking_status, f.departure_datetime " +
                "FROM bookings b JOIN flights f ON b.flight_id = f.id " +
                "WHERE b.id = ? AND b.user_id = ? FOR UPDATE";
        String updateBookingSQL = "UPDATE bookings SET booking_status = 'CANCELLED' WHERE id = ?";
        String restoreSeatsSQL = "UPDATE flights SET available_seats = available_seats + ? WHERE id = (SELECT flight_id FROM bookings WHERE id = ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int seatsBooked = 0;
            String status = "";
            Timestamp departureTime = null;

            try (PreparedStatement stmt = conn.prepareStatement(getBookingSQL)) {
                stmt.setInt(1, bookingId);
                stmt.setInt(2, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        seatsBooked = rs.getInt("seats_booked");
                        status = rs.getString("booking_status");
                        departureTime = rs.getTimestamp("departure_datetime");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (!"CONFIRMED".equalsIgnoreCase(status)) {
                conn.rollback();
                return false;
            }

            // 24-hour rule validation
            long diffMillis = departureTime.getTime() - System.currentTimeMillis();
            long hoursLeft = diffMillis / (1000 * 60 * 60);
            if (hoursLeft < 24) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateBookingSQL)) {
                stmt.setInt(1, bookingId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(restoreSeatsSQL)) {
                stmt.setInt(1, seatsBooked);
                stmt.setInt(2, bookingId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.id, b.seats_booked, b.total_amount, b.booking_status, " +
                "f.flight_code, f.origin, f.destination, f.departure_datetime " +
                "FROM bookings b JOIN flights f ON b.flight_id = f.id " +
                "WHERE b.user_id = ? ORDER BY b.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setId(rs.getInt("id"));
                    b.setSeatsBooked(rs.getInt("seats_booked"));
                    b.setTotalAmount(rs.getDouble("total_amount"));
                    b.setBookingStatus(rs.getString("booking_status"));
                    b.setFlightCode(rs.getString("flight_code"));
                    b.setOrigin(rs.getString("origin"));
                    b.setDestination(rs.getString("destination"));
                    b.setDepartureDatetime(rs.getTimestamp("departure_datetime").toString());
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}