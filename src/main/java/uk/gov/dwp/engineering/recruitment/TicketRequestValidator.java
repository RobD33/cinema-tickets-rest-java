package uk.gov.dwp.engineering.recruitment;

import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
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
        }
    }
}
