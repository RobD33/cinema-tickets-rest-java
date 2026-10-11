package uk.gov.dwp.engineering.recruitment;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

@Service
public class CinemaTicketsServiceImpl implements CinemaTicketsService {

  private static final BigDecimal ADULT_PRICE = new BigDecimal("25.99");
  private static final BigDecimal CHILD_PRICE = new BigDecimal("17.50");
  private static final BigDecimal INFANT_PRICE = BigDecimal.ZERO;

  private final PaymentService paymentService;

  private final SeatReservationService seatReservationService;

  public CinemaTicketsServiceImpl(PaymentService paymentService,
      SeatReservationService seatReservationService) {
    this.paymentService = paymentService;
    this.seatReservationService = seatReservationService;
  }

  @Override
  public BookingConfirmation purchaseTickets(final Long accountId,
      final TicketRequest... ticketRequests)
      throws InvalidBookingException {

        TicketRequestValidator validator =
        new TicketRequestValidator(accountId, ticketRequests);

        TicketCounts counts = validator.validateTicketRequest();

        BigDecimal paymentAmount = calculatePayment(counts);

        long seatsToReserve = calculateSeatsToReserve(counts);

        paymentService.debitAccount(accountId, paymentAmount);

        seatReservationService.reserveSeats(accountId, seatsToReserve);

        return new BookingConfirmation(accountId, seatsToReserve, paymentAmount);
  }

  private BigDecimal calculatePayment(TicketCounts counts) {
    return ADULT_PRICE.multiply(BigDecimal.valueOf(counts.adults()))
            .add(CHILD_PRICE.multiply(BigDecimal.valueOf(counts.children())))
            .add(INFANT_PRICE.multiply(BigDecimal.valueOf(counts.infants())));
  }

  private long calculateSeatsToReserve(TicketCounts counts) {
    return counts.adults() + counts.children();
  }
}
