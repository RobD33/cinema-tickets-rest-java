# Cinema Tickets Code Test

### Requirements

| Scenario                           | Expected result                  |
| ---------------------------------- | ---------------------------------|
| Account ID is zero or negative     | Reject booking                   |
| Empty booking or zero tickets      | Reject booking                   |
| Negative or zero ticket count      | Reject booking                   |
| No ADULT ticket                    | Reject booking                   |
| More INFANT than ADULT             | Reject booking                   |
| More than 25 tickets               | Reject booking                   |
| Valid booking                      | Process payment and reserve seats|


### Assumptions

- One infant per adult lap
- Requests with negative ticket count should be rejected
- Request with zero tickets requests should be rejected
- openapi spec is out of line with controller - using code as source of truth.

### Approach

- Turn specifications into requirements
- TDD
- Request validation, create new validation class for separation of concerns.
- integrate validation class and integration test behaviour.
- Calculate payment amount and integrate payments service.
- Calculate number of seats and itegrate reservation service.
- Due to simplicity and single use these calculations are not separated into distinct classes.
- Add mock third party services to integration tests and verify interaction.

## Building and Running the Application

### Prerequisites

Install

- Java 21
- Apache Maven

### Build the Application

Run

```bash
mvn clean package
```

This cleans previous build output, compiles the application, and packages it into a JAR file.

### Run the Application

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

### Run the Unit Tests

```bash
mvn test
```

These tests cover ticket validation, payment calculations, seat reservation calculations.

### Run the Integration Tests

```bash
mvn verify
```

The integration tests start the application on a random port and send HTTP requests to the booking endpoint. They verify successful and invalid booking scenarios, and interactions with the payment and seat reservation services.
