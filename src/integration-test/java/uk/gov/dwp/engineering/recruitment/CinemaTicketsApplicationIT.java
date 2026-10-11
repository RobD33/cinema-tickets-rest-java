package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(
    classes = CinemaTicketsApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CinemaTicketsControllerIT {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private HttpResponse<String> postBooking(String requestBody)
            throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(
                "http://localhost:" + port + "/cinema/bookings"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        return httpClient.send(
            request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void shouldReturnCreatedWhenBookingAdultAndChildTickets()
            throws Exception {
        HttpResponse<String> response = postBooking("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 1},
                {"type": "CHILD", "ticketCount": 1}
              ]
            }
            """);

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("\"accountId\":123"));
    }

    @Test
    void shouldReturnCreatedWhenBookingAdultTicketsOnly()
            throws Exception {
        HttpResponse<String> response = postBooking("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 2}
              ]
            }
            """);

        assertEquals(201, response.statusCode());
    }
}