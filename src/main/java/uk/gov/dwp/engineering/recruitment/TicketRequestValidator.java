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
    }

    private void validateAccountId() throws InvalidBookingException {
        if (accountId == null || accountId <= 0) {
            throw new InvalidBookingException(
                    "Account ID must be a positive integer."
            );
        }
    }
}