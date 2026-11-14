SkyWings – AWD Practical Notes
Project overview This project is a Java web application for an airline booking system built with:
•Java 17
•Maven
•Jakarta Servlet / JSP
•Tomcat 11
•MySQL
•JSTL

Main features
1.User login and registration
2.Passenger flight listing/search by city
3.Booking flights
4.Viewing booked flights
5.Cancelling bookings (24-hour rule enforced)
6.Agent/admin flight management
7.Role-based access control

Important project structure src/main/java/bac/ac/skywings/   
controller/     AuthServlet.java     FlightsServlet.java     BookingServlet.java     FlightManagementServlet.java     HomeController.java   
dao/     UserDAO.java     FlightDAO.java     BookingDAO.java   
filter/     RoleFilter.java   
model/     User.java     Flight.java     Booking.java   util/     DBConnection.java

Web UI JSP files live here: src/main/webapp/WEB-INF/views/
•login.jsp
•register.jsp
•flights.jsp
•my-bookings.jsp
•agent-flights.jsp

Main URL routes
•/login
•/register
•/logout
•/flights
•/book
•/my-bookings
•/agent/flights

Important notes for running the app
1.Tomcat must be running and the project should be deployed as a WAR.
2.The project uses MySQL at:    jdbc:mysql://localhost:3306/skywings_db?useSSL=false&serverTimezone=UTC
3.Database user:    username: root    password: bianca.1865
4.JSTL dependencies must be included in the Maven build. Without the JSTL API jar, JSP pages fail with:    java.lang.NoClassDefFoundError: jakarta.servlet.jsp.jstl.core.ConditionalTagSupport
5.If you see the old JSP class error after changing JSP paths, fully clear Tomcat:
◦delete webapps/SkyWings and webapps/SkyWings.war
◦delete Tomcat/work/Catalina/localhost/SkyWings
◦redeploy the project
6.JSP paths should be under /WEB-INF/views, not /WEB-INF directly.

Key code snippets
1.Login / registration servlet routing
   @WebServlet(urlPatterns = {"/login", "/register", "/logout"})    public class AuthServlet extends HttpServlet {        @Override        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {            String path = request.getServletPath();
           switch (path) {               case "/login":                   request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);                   break;               case "/register":                   request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);                   break;               case "/logout":                   HttpSession session = request.getSession(false);                   if (session != null) session.invalidate();                   response.sendRedirect(request.getContextPath() + "/login");                   break;           }       }
   }
   
2.Login logic with role redirect
   User user = userDAO.findByEmail(email);    if (user != null && user.getPassword().equals(password)) {        HttpSession session = request.getSession();        session.setAttribute("currentUser", user);
       if ("ADMIN".equalsIgnoreCase(user.getRole()) || "AIRLINE_AGENT".equalsIgnoreCase(user.getRole())) {           response.sendRedirect(request.getContextPath() + "/agent/flights");       } else {           response.sendRedirect(request.getContextPath() + "/flights");       }
   }
   
3.Flight listing servlet / route
   @WebServlet("/flights")    public class FlightsServlet extends HttpServlet {        @Override        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {            HttpSession session = request.getSession(false);            User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
           if (user == null) {               response.sendRedirect(request.getContextPath() + "/login");               return;           }
           String origin = request.getParameter("origin");           String destination = request.getParameter("destination");
           request.setAttribute("flights", flightDAO.searchFlights(origin, destination));           request.getRequestDispatcher("/WEB-INF/views/flights.jsp").forward(request, response);       }
   }
   
4.DAO query for flight search
   StringBuilder sql = new StringBuilder("SELECT * FROM flights WHERE departure_datetime > NOW()");
   boolean hasOrigin = (origin != null && !origin.trim().isEmpty() && !origin.equals("ALL"));    boolean hasDest = (destination != null && !destination.trim().isEmpty() && !destination.equals("ALL"));
   if (hasOrigin) sql.append(" AND origin LIKE ?");    if (hasDest) sql.append(" AND destination LIKE ?");
   
5.Booking creation with seat check and transaction
   String checkSeatsSQL = "SELECT available_seats, ticket_price FROM flights WHERE id = ? FOR UPDATE";    String updateSeatsSQL = "UPDATE flights SET available_seats = available_seats - ? WHERE id = ?";    String insertBookingSQL = "INSERT INTO bookings (user_id, flight_id, seats_booked, total_amount, booking_status) VALUES (?, ?, ?, ?, 'CONFIRMED')";
   conn.setAutoCommit(false);    // Validate seats, update seats, insert booking, commit
   
6.Cancellation rule (24 hours before departure)
   String getBookingSQL = "SELECT b.seats_booked, b.booking_status, f.departure_datetime " +            "FROM bookings b JOIN flights f ON b.flight_id = f.id " +            "WHERE b.id = ? AND b.user_id = ? FOR UPDATE";
   long diffMillis = departureTime.getTime() - System.currentTimeMillis();    long hoursLeft = diffMillis / (1000 * 60 * 60);
   if (hoursLeft < 24) {        conn.rollback();        return false;    }
   
7.Role filter sample
   @WebFilter(urlPatterns = {"/flights", "/book", "/my-bookings", "/agent/", "/admin/"})    public class RoleFilter implements Filter {        if (user == null) {            res.sendRedirect(req.getContextPath() + "/login?error=unauthorized");            return;        }
       if (uri.contains("/agent/") && !"AIRLINE_AGENT".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {           res.sendRedirect(req.getContextPath() + "/flights?error=forbidden");           return;       }
   }
   
Database schema and SQL used Main tables used:
•users
•flights
•bookings

Example CREATE TABLE statements used
1.Flights table
   CREATE TABLE IF NOT EXISTS flights (        id INT AUTO_INCREMENT PRIMARY KEY,        flight_code VARCHAR(20),        origin VARCHAR(100),        destination VARCHAR(100),        departure_datetime DATETIME,        total_seats INT,        available_seats INT,        ticket_price DECIMAL(10,2)    );

2.Users table
   CREATE TABLE IF NOT EXISTS users (        id INT AUTO_INCREMENT PRIMARY KEY,        email VARCHAR(255),        full_name VARCHAR(255),        password VARCHAR(255),        role VARCHAR(50)    );

3.Bookings table
   CREATE TABLE IF NOT EXISTS bookings (        id INT AUTO_INCREMENT PRIMARY KEY,        user_id INT,        flight_id INT,        seats_booked INT,        total_amount DECIMAL(10,2),        booking_status VARCHAR(20),        FOREIGN KEY (user_id) REFERENCES users(id),        FOREIGN KEY (flight_id) REFERENCES flights(id)    );

Key SQL queries used in the app
1.Register a user
   INSERT INTO users (email, full_name, password, role)    VALUES (?, ?, ?, ?);

2.Find user by email
   SELECT * FROM users WHERE email = ?;

3.Fetch available flights
   SELECT * FROM flights    WHERE departure_datetime > NOW()    AND origin LIKE ?    AND destination LIKE ?;

4.Insert a new flight
   INSERT INTO flights (        flight_code, origin, destination, departure_datetime,        total_seats, available_seats, ticket_price    ) VALUES (?, ?, ?, ?, ?, ?, ?);

5.Book a flight
   INSERT INTO bookings (user_id, flight_id, seats_booked, total_amount, booking_status)    VALUES (?, ?, ?, ?, 'CONFIRMED');

6.Reduce available seats after booking
   UPDATE flights SET available_seats = available_seats - ? WHERE id = ?;

7.Get booking data for a user
   SELECT b.id, b.seats_booked, b.total_amount, b.booking_status,           f.flight_code, f.origin, f.destination, f.departure_datetime    FROM bookings b    JOIN flights f ON b.flight_id = f.id    WHERE b.user_id = ?    ORDER BY b.id DESC;

8.Cancel booking
   UPDATE bookings SET booking_status = 'CANCELLED' WHERE id = ?;

9.Restore seats after cancelling
   UPDATE flights SET available_seats = available_seats + ? WHERE id = (SELECT flight_id FROM bookings WHERE id = ?);
   
Seeded sample data used during testing Login credentials used:
•Email: passenger@test.com
•Password: 1234
Sample Botswana routes inserted:
•BW101 | Gaborone -> Francistown
•BW102 | Francistown -> Maun
•BW103 | Maun -> Kasane
•BW104 | Kasane -> Gaborone
•BW105 | Gaborone -> Maun
•BW106 | Gaborone -> Kasane

Sample booking inserted for testing:
•user: passenger@test.com
•flight: BW200
•route: Gaborone -> Kasane
•seats: 2
•status: CONFIRMED

Sample SQL used to seed the data INSERT INTO flights (   flight_code, origin, destination, departure_datetime,   total_seats, available_seats, ticket_price ) VALUES ('BW101', 'Gaborone', 'Francistown', '2026-12-15 08:30:00', 80, 80, 1200.00), ('BW102', 'Francistown', 'Maun', '2026-12-16 09:15:00', 70, 70, 1400.00), ('BW103', 'Maun', 'Kasane', '2026-12-17 11:00:00', 65, 65, 1600.00), ('BW104', 'Kasane', 'Gaborone', '2026-12-18 13:45:00', 90, 90, 1800.00), ('BW105', 'Gaborone', 'Maun', '2026-12-19 16:20:00', 75, 75, 1700.00), ('BW106', 'Gaborone', 'Kasane', '2026-12-20 18:10:00', 85, 85, 2000.00);
INSERT INTO users (email, full_name, password, role) VALUES ('passenger@test.com', 'Test Passenger', '1234', 'PASSENGER');
INSERT INTO bookings (user_id, flight_id, seats_booked, total_amount, booking_status) VALUES (   (SELECT id FROM users WHERE email='passenger@test.com'),   (SELECT id FROM flights WHERE flight_code='BW200'),   2, 4400.00, 'CONFIRMED' );

Common errors and fixes
1.404 JSP not found
◦JSP file path must be /WEB-INF/views/login.jsp
◦not /WEB-INF/login.jsp

2.Jasper ClassNotFoundException
◦Clear Tomcat work folder and webapps folder
◦Rebuild and redeploy WAR

3.NoClassDefFoundError for JSTL
◦Add both dependencies:
▪jakarta.servlet.jsp.jstl-api
▪org.glassfish.web:jakarta.servlet.jsp.jstl

4.Flights page is empty
◦Check database has flight rows
◦Make sure departure_datetime is in the future
◦Ensure origin/destination values exist in DB

5.
My Bookings page empty
◦Need a user with a valid booking record for that user_id

6.
Cancellation fails
◦Booking must be confirmed
◦Flight must be at least 24 hours away
◦Use the correct user id for the booking

Quick practical checklist
•Start MySQL and verify skywings_db exists
•Rebuild WAR with Maven
•Deploy to Tomcat
•Start Tomcat
•Test login
•Search flights by city
•Create booking
•View my bookings
•Cancel booking

Important final note This project was intentionally used as a code vault and practical revision project, so the logic is intentionally simple and transparent. If you need to present it in an exam, explain that the app uses:
•
a servlet-based MVC flow,
•
DAO layer for database operations,
•
SQL transactions for booking integrity,
•
role-based filtering for access control,
•
JSP pages for UI rendering.
