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

        validator.validateTicketRequest();

        BigDecimal paymentAmount = calculatePayment(ticketRequests);

        paymentService.debitAccount(accountId, paymentAmount);

        return new BookingConfirmation(accountId);
  }

  private BigDecimal calculatePayment(
            final TicketRequest... ticketRequests) {

        BigDecimal total = BigDecimal.ZERO;

        for (TicketRequest request : ticketRequests) {
            BigDecimal ticketPrice = switch (request.type()) {
                case ADULT -> ADULT_PRICE;
                case CHILD -> CHILD_PRICE;
                case INFANT -> INFANT_PRICE;
            };

            BigDecimal requestTotal = ticketPrice.multiply(
                    BigDecimal.valueOf(request.ticketCount()));

            total = total.add(requestTotal);
        }

        return total;
    }
}
