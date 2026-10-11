package uk.gov.dwp.engineering.recruitment;

import java.util.Arrays;

import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;

public class TicketRequestValidator {

    private final Long accountId;
    private final TicketRequest[] ticketRequests;

    public TicketRequestValidator(
            Long accountId,
            TicketRequest... ticketRequests) {
        this.accountId = accountId;
        this.ticketRequests = ticketRequests;
    }

    public void validateTicketRequest() throws InvalidBookingException {
        validateAccountId();
        validateTicketRequests();
        validateAdultTicketPresent();
        validateAdultToInfantRatio();
    }

    private void validateAccountId() throws InvalidBookingException {
        if (accountId == null || accountId <= 0) {
            throw new InvalidBookingException(
                    "Account ID must be a positive integer."
            );
        }
    }

    private void validateTicketRequests() throws InvalidBookingException {
        if (ticketRequests == null || ticketRequests.length == 0) {
            throw new InvalidBookingException(
                    "At least one ticket request must be provided."
            );
        }

        for (TicketRequest ticketRequest : ticketRequests) {
            if (ticketRequest == null) {
                throw new InvalidBookingException(
                        "Ticket requests must not contain null values."
                );
            }

            if (ticketRequest.ticketCount() <= 0) {
                throw new InvalidBookingException(
                    "Ticket count must be a positive integer."
                );
            }
        }
    }

    private void validateAdultTicketPresent() throws InvalidBookingException {
        boolean hasAdultTicket = Arrays.stream(ticketRequests)
                .anyMatch(request ->
                        request.type() == TicketType.ADULT
                        && request.ticketCount() > 0);

        if (!hasAdultTicket) {
            throw new InvalidBookingException(
                    "At least one adult ticket must be purchased."
            );
        }
    }

    private void validateAdultToInfantRatio() throws InvalidBookingException {
        int adultCount = Arrays.stream(ticketRequests)
                .filter(request -> request.type() == TicketType.ADULT)
                .mapToInt(TicketRequest::ticketCount)
                .sum();

        int infantCount = Arrays.stream(ticketRequests)
                .filter(request -> request.type() == TicketType.INFANT)
                .mapToInt(TicketRequest::ticketCount)
                .sum();

        if (adultCount < infantCount) {
            throw new InvalidBookingException(
                    "There must be at least as many adult tickets as infant tickets."
            );
        }
    }
}
