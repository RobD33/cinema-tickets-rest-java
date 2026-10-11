package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.Test;

import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketRequestValidatorTest {

    @Test
    void shouldAcceptPositiveAccountId() {
        TicketRequestValidator validator =
                new TicketRequestValidator(1L);

        assertDoesNotThrow(validator::validateTicketRequest);
    }

   @Test
    void shouldRejectZeroAccountId() {
        TicketRequestValidator validator =
                new TicketRequestValidator(0L);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectNegativeAccountId() {
        TicketRequestValidator validator =
                new TicketRequestValidator(-1L);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectNullAccountId() {
        TicketRequestValidator validator =
                new TicketRequestValidator((Long) null);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }
}
