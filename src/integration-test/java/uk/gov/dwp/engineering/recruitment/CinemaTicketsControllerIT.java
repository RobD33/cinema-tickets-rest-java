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

    private void assertBadRequest(String requestBody) throws Exception {
        HttpResponse<String> response = postBooking(requestBody);

        assertEquals(400, response.statusCode(),
            "Expected HTTP 400 but received "
                + response.statusCode()
                + ". Response body: " + response.body());
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

    @Test
    void shouldRejectZeroAccountId() throws Exception {
        assertBadRequest("""
            {
              "accountId": 0,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 1}
              ]
            }
            """);
    }

    @Test
    void shouldRejectNegativeAccountId() throws Exception {
        assertBadRequest("""
            {
              "accountId": -1,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 1}
              ]
            }
            """);
    }

    @Test
    void shouldRejectMissingAccountId() throws Exception {
        assertBadRequest("""
            {
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 1}
              ]
            }
            """);
    }

    @Test
    void shouldRejectEmptyTicketRequests() throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": []
            }
            """);
    }

    @Test
    void shouldRejectMissingTicketRequests() throws Exception {
        assertBadRequest("""
            {
              "accountId": 123
            }
            """);
    }

    @Test
    void shouldRejectChildTicketsWithoutAdultTickets()
            throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "CHILD", "ticketCount": 1}
              ]
            }
            """);
    }

    @Test
    void shouldRejectInfantTicketsWithoutAdultTickets()
            throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "INFANT", "ticketCount": 1}
              ]
            }
            """);
    }

    @Test
    void shouldRejectMoreInfantsThanAdults() throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 1},
                {"type": "INFANT", "ticketCount": 2}
              ]
            }
            """);
    }

    @Test
    void shouldAllowEqualNumbersOfAdultsAndInfants()
            throws Exception {
        HttpResponse<String> response = postBooking("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 2},
                {"type": "INFANT", "ticketCount": 2}
              ]
            }
            """);

        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldRejectMoreThan25Tickets() throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 26}
              ]
            }
            """);
    }

    @Test
    void shouldRejectMoreThan25TicketsAcrossMultipleRequests()
            throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 1},
                {"type": "CHILD", "ticketCount": 25}
              ]
            }
            """);
    }

    @Test
    void shouldRejectZeroTicketCount() throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": 0}
              ]
            }
            """);
    }

    @Test
    void shouldRejectNegativeTicketCount() throws Exception {
        assertBadRequest("""
            {
              "accountId": 123,
              "ticketRequests": [
                {"type": "ADULT", "ticketCount": -1}
              ]
            }
            """);
    }
}