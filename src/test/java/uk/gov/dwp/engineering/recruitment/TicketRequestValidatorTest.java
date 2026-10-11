package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.Test;

import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketRequestValidatorTest {

    @Test
    void shouldAcceptPositiveAccountId() {
        TicketRequest validRequest =
                new TicketRequest(TicketType.ADULT, 1);
        TicketRequestValidator validator =
                new TicketRequestValidator(1L, validRequest);

        assertDoesNotThrow(validator::validateTicketRequest);
    }

   @Test
    void shouldRejectZeroAccountId() {
        TicketRequest validRequest =
                new TicketRequest(TicketType.ADULT, 1);
        TicketRequestValidator validator =
                new TicketRequestValidator(0L, validRequest);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectNegativeAccountId() {
        TicketRequest validRequest =
                new TicketRequest(TicketType.ADULT, 1);
        TicketRequestValidator validator =
                new TicketRequestValidator(-1L, validRequest);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectNullAccountId() {
        TicketRequest validRequest =
                new TicketRequest(TicketType.ADULT, 1);
        TicketRequestValidator validator =
                new TicketRequestValidator((Long) null, validRequest);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectNullTicketRequestsArray() {
        TicketRequestValidator validator =
                new TicketRequestValidator(1L, (TicketRequest[]) null);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectEmptyTicketRequestsArray() {
        TicketRequestValidator validator =
                new TicketRequestValidator(1L);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectTicketRequestsContainingNull() {
        TicketRequestValidator validator =
                new TicketRequestValidator(1L, (TicketRequest) null);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectTicketRequestsContainingNullAlongsideValidRequest() {
        TicketRequest validRequest =
                new TicketRequest(TicketType.ADULT, 1);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, validRequest, null);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldAcceptNonNullTicketRequests() {
        TicketRequest firstRequest =
                new TicketRequest(TicketType.ADULT, 1);
        TicketRequest secondRequest =
                new TicketRequest(TicketType.CHILD, 2);

        TicketRequestValidator validator =
                new TicketRequestValidator(
                        1L, firstRequest, secondRequest);

        assertDoesNotThrow(validator::validateTicketRequest);
    }
}
