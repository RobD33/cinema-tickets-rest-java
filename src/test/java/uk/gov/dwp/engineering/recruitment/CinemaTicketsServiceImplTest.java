package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

@ExtendWith(MockitoExtension.class)
class CinemaTicketsServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private SeatReservationService seatReservationService;

    private CinemaTicketsServiceImpl cinemaTicketsService;

    @BeforeEach
    void setUp() {
        cinemaTicketsService = new CinemaTicketsServiceImpl(
                paymentService, seatReservationService);
    }

    @Test
    void shouldCalculatePaymentForOneAdult()
            throws InvalidBookingException {
        when(paymentService.debitAccount(
                anyLong(), any(BigDecimal.class)))
                .thenReturn(ResponseEntity.ok("Payment successful"));
        purchaseAndVerifyPayment(
                "25.99",
                new TicketRequest(TicketType.ADULT, 1));
    }

    @Test
    void shouldCalculatePaymentForMultipleAdults()
            throws InvalidBookingException {
        when(paymentService.debitAccount(
                anyLong(), any(BigDecimal.class)))
                .thenReturn(ResponseEntity.ok("Payment successful"));
        purchaseAndVerifyPayment(
                "51.98",
                new TicketRequest(TicketType.ADULT, 2));
    }

    @Test
    void shouldCalculatePaymentForAdultAndChild()
            throws InvalidBookingException {
        when(paymentService.debitAccount(
                anyLong(), any(BigDecimal.class)))
                .thenReturn(ResponseEntity.ok("Payment successful"));
        purchaseAndVerifyPayment(
                "43.49",
                new TicketRequest(TicketType.ADULT, 1),
                new TicketRequest(TicketType.CHILD, 1));
    }

    @Test
    void shouldNotChargeForInfantTickets() throws InvalidBookingException {
        when(paymentService.debitAccount(
                anyLong(), any(BigDecimal.class)))
                .thenReturn(ResponseEntity.ok("Payment successful"));
        purchaseAndVerifyPayment(
                "25.99",
                new TicketRequest(TicketType.ADULT, 1),
                new TicketRequest(TicketType.INFANT, 1));
    }

    @Test
    void shouldCalculatePaymentForMultipleTicketTypes()
            throws InvalidBookingException {
        when(paymentService.debitAccount(
                anyLong(), any(BigDecimal.class)))
                .thenReturn(ResponseEntity.ok("Payment successful"));
        purchaseAndVerifyPayment(
                "104.48",
                new TicketRequest(TicketType.ADULT, 2),
                new TicketRequest(TicketType.CHILD, 3),
                new TicketRequest(TicketType.INFANT, 2));
    }

    @Test
    void shouldNotCallPaymentWhenBookingIsInvalid() {
        TicketRequest child = new TicketRequest(TicketType.CHILD, 1);

        org.junit.jupiter.api.Assertions.assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(123L, child));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    private void purchaseAndVerifyPayment(
        String expectedAmount,
        TicketRequest... ticketRequests
    ) throws InvalidBookingException {

        BookingConfirmation confirmation =
                cinemaTicketsService.purchaseTickets(123L, ticketRequests);

        assertEquals(123L, confirmation.accountId());

        verify(paymentService).debitAccount(
                org.mockito.ArgumentMatchers.eq(123L),
                org.mockito.ArgumentMatchers.eq(
                        new BigDecimal(expectedAmount)));
    }
}
