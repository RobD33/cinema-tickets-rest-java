package uk.gov.dwp.engineering.recruitment;

import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;

public class TicketRequestValidator {

    private static final int MAX_TICKETS = 25;

    private final Long accountId;
    private final TicketRequest[] ticketRequests;

    public TicketRequestValidator(
            Long accountId,
            TicketRequest... ticketRequests) {
        this.accountId = accountId;
        this.ticketRequests = ticketRequests;
    }

    public TicketCounts validateTicketRequest() throws InvalidBookingException {
        validateAccountId();
        validateTicketRequests();

        TicketCounts counts = TicketCounts.from(ticketRequests);

        validateAdultTicketPresent(counts);
        validateAdultToInfantRatio(counts);
        validateMaximumTicketCount(counts);

        return counts;
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

    private void validateAdultTicketPresent(TicketCounts counts) throws InvalidBookingException {
        if (counts.adults() == 0) {
            throw new InvalidBookingException(
                    "At least one adult ticket is required.");
        }
    }

    private void validateAdultToInfantRatio(TicketCounts counts) throws InvalidBookingException {
        if (counts.adults() < counts.infants()) {
            throw new InvalidBookingException(
                "There must be at least as many adult tickets as infant tickets."
            );
        }
    }

    private void validateMaximumTicketCount(TicketCounts counts) throws InvalidBookingException {
        long totalTicketCount = counts.adults() + counts.children() + counts.infants();

        if (totalTicketCount > MAX_TICKETS) {
            throw new InvalidBookingException(
                    "A maximum of " + MAX_TICKETS + " tickets can be purchased at a time."
                );
        }   
    }
}
