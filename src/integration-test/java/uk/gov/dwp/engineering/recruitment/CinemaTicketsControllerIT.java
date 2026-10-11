package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

@SpringBootTest(
    classes = CinemaTicketsApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CinemaTicketsControllerIT {

  @MockitoBean
  private PaymentService paymentService;

  @MockitoBean
  private SeatReservationService seatReservationService;

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

  private void assertBadRequest(String requestBody, String expectedErrorMessage) throws Exception {
      HttpResponse<String> response = postBooking(requestBody);

      assertEquals(400, response.statusCode(),
          "Expected HTTP 400 but received "
            + response.statusCode()
            + ". Response body: " + response.body());
      
      assertTrue(response.body().contains(expectedErrorMessage),
          "Expected response body to contain: "
            + expectedErrorMessage
            + ". Actual body: " + response.body());

      verifyNoInteractions(paymentService, seatReservationService);
  }

  private void stubSuccessfulBooking() {
    when(paymentService.debitAccount(
            anyLong(), any(BigDecimal.class)))
            .thenReturn(ResponseEntity.ok("Payment successful"));

    when(seatReservationService.reserveSeats(
            anyLong(), anyLong()))
            .thenReturn(ResponseEntity.ok("Seats reserved"));
  }

  private void verifyBookingServices(BigDecimal expectedAmount, long expectedSeats) {
    verify(paymentService).debitAccount(
            123L, expectedAmount);

    verify(seatReservationService).reserveSeats(
            123L, expectedSeats);
  }

  @Test
  void shouldReturnCreatedWhenBookingAdultAndChildTickets()
          throws Exception {
      stubSuccessfulBooking();
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

      verifyBookingServices(new BigDecimal("43.49"), 2L);
  }

  @Test
  void shouldReturnCreatedWhenBookingAdultTicketsOnly()
          throws Exception {
      stubSuccessfulBooking();
      HttpResponse<String> response = postBooking("""
          {
            "accountId": 123,
            "ticketRequests": [
              {"type": "ADULT", "ticketCount": 2}
            ]
          }
          """);

      assertEquals(201, response.statusCode());
      verifyBookingServices(new BigDecimal("51.98"), 2L);
  }

  @Test
  void shouldReturnCreatedWhenBookingAdultChildAndInfantTickets()
          throws Exception {
      stubSuccessfulBooking();
      HttpResponse<String> response = postBooking("""
          {
            "accountId": 123,
            "ticketRequests": [
              {"type": "ADULT", "ticketCount": 3},
              {"type": "CHILD", "ticketCount": 5},
              {"type": "INFANT", "ticketCount": 2}
            ]
          }
          """);

      assertEquals(201, response.statusCode());
      assertTrue(response.body().contains("\"accountId\":123"));

      verifyBookingServices(new BigDecimal("165.47"), 8L);
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
          """, 
          "Account ID must be a positive integer."
        );
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
          """,
          "Account ID must be a positive integer."
        );
  }

  @Test
  void shouldRejectMissingAccountId() throws Exception {
      assertBadRequest("""
          {
            "ticketRequests": [
              {"type": "ADULT", "ticketCount": 1}
            ]
          }
          """,
          "Account ID must be a positive integer."
        );
  }

  @Test
  void shouldRejectEmptyTicketRequests() throws Exception {
      assertBadRequest("""
          {
            "accountId": 123,
            "ticketRequests": []
          }
          """,
          "At least one ticket request must be provided."
        );
  }

  @Test
  void shouldRejectMissingTicketRequests() throws Exception {
      assertBadRequest("""
          {
            "accountId": 123
          }
          """,
          "At least one ticket request must be provided."
        );
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
          """,
          "At least one adult ticket is required."
        );
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
          """,
          "At least one adult ticket is required."
        );
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
          """,
          "There must be at least as many adult tickets as infant tickets."
        );
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
          """,
          "A maximum of 25 tickets can be purchased at a time."
        );
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
          """,
          "A maximum of 25 tickets can be purchased at a time."
        );
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
          """,
          "Ticket count must be a positive integer."
        );
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
          """,
          "Ticket count must be a positive integer."
        );
  }
}