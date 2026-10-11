package uk.gov.dwp.engineering.recruitment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class BookingConfirmationTest {

  @Test
  void givenBookingConfirmation_whenGetAccount_thenExpectedValueReturned() {
    final BookingConfirmation bookingConfirmation = new BookingConfirmation(
      99L,
      5L,
      new BigDecimal("165.47")
    );
    assertEquals(99L, bookingConfirmation.accountId());
    assertEquals(5L, bookingConfirmation.seatCount());
    assertEquals(new BigDecimal("165.47"), bookingConfirmation.totalCost());
  }
}
