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

    @Test
    void shouldRejectTicketRequestsContainingZeroTicketCount() {
        TicketRequest firstRequest =
                new TicketRequest(TicketType.ADULT, 1);
                TicketRequest secondRequest =
                new TicketRequest(TicketType.CHILD, 3);
        TicketRequest thirdRequest =
                new TicketRequest(TicketType.CHILD, 0);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, firstRequest, secondRequest, thirdRequest);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectTicketRequestsContainingNegativeTicketCount() {
        TicketRequest firstRequest =
                new TicketRequest(TicketType.ADULT, 1);
                TicketRequest secondRequest =
                new TicketRequest(TicketType.CHILD, 3);
        TicketRequest thirdRequest =
                new TicketRequest(TicketType.CHILD, -2);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, firstRequest, secondRequest, thirdRequest);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectBookingWithOnlyChildTickets() {
        TicketRequest childRequest =
                new TicketRequest(TicketType.CHILD, 1);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, childRequest);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldRejectBookingWithOnlyInfantTickets() {
        TicketRequest infantRequest =
                new TicketRequest(TicketType.INFANT, 1);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, infantRequest);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldAcceptBookingWithMoreAdultsThanInfants() {
        TicketRequest adults = new TicketRequest(TicketType.ADULT, 3);
        TicketRequest infants = new TicketRequest(TicketType.INFANT, 2);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, adults, infants);

        assertDoesNotThrow(validator::validateTicketRequest);
    }

    @Test
    void shouldAcceptBookingWithEqualNumbersOfAdultsAndInfants() {
        TicketRequest adults = new TicketRequest(TicketType.ADULT, 2);
        TicketRequest infants = new TicketRequest(TicketType.INFANT, 2);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, adults, infants);

        assertDoesNotThrow(validator::validateTicketRequest);
    }

    @Test
    void shouldRejectBookingWithMoreInfantsThanAdults() {
        TicketRequest adults = new TicketRequest(TicketType.ADULT, 1);
        TicketRequest infants = new TicketRequest(TicketType.INFANT, 2);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, adults, infants);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldAcceptBookingWith24Tickets() {
        TicketRequest adults = new TicketRequest(TicketType.ADULT, 24);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, adults);

        assertDoesNotThrow(validator::validateTicketRequest);
    }

    @Test
    void shouldAcceptBookingWithExactly25Tickets() {
        TicketRequest adults = new TicketRequest(TicketType.ADULT, 25);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, adults);

        assertDoesNotThrow(validator::validateTicketRequest);
    }

    @Test
    void shouldRejectBookingWith26Tickets() {
        TicketRequest adults = new TicketRequest(TicketType.ADULT, 26);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, adults);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }

    @Test
    void shouldCountTicketsAcrossMultipleRequests() {
        TicketRequest adults = new TicketRequest(TicketType.ADULT, 13);
        TicketRequest children = new TicketRequest(TicketType.CHILD, 10);
        TicketRequest infants = new TicketRequest(TicketType.INFANT, 3);

        TicketRequestValidator validator =
                new TicketRequestValidator(1L, adults, children, infants);

        assertThrows(
                InvalidBookingException.class,
                validator::validateTicketRequest
        );
    }
}
