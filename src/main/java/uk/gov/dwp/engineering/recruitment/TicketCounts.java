package uk.gov.dwp.engineering.recruitment;

import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;

public record TicketCounts(
        long adults,
        long children,
        long infants) {

    public static TicketCounts from(TicketRequest... requests) {
        long adults = 0;
        long children = 0;
        long infants = 0;

        for (TicketRequest request : requests) {
            switch (request.type()) {
                case ADULT -> adults += request.ticketCount();
                case CHILD -> children += request.ticketCount();
                case INFANT -> infants += request.ticketCount();
            }
        }

        return new TicketCounts(adults, children, infants);
    }
}