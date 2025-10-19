### Design & Assumptions

- The seat bookings are stored in-memory (BookingStore) and it's not persistent long-term
- The BookingService handles the seat allocation logic
- Single seat can be left unoccupied

### Environment & Prerequisites

- Used IntelliJ on Mac system
- Maven was used for build and dependency management
- Java version - 21
- No database is used

### Execution

From the root directory;

- Use Maven to compile: `mvn clean complie`
- Use Maven to run tests: `mvn test`
- To run the application: `java -cp "target/classes:target/dependency/*" com.cinema.TicketingSystem`