package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
    void shouldCalculatePaymentAndReserveOneSeatForOneAdult()
            throws InvalidBookingException {
        purchaseAndVerifyBooking(
                "25.99",
                1L,
                new TicketRequest(TicketType.ADULT, 1));
    }

    @Test
    void shouldCalculatePaymentAndReserveSeatsForMultipleAdults()
            throws InvalidBookingException {
        purchaseAndVerifyBooking(
                "51.98",
                2L,
                new TicketRequest(TicketType.ADULT, 2));
    }

    @Test
    void shouldCalculatePaymentAndReserveSeatsForAdultAndChild()
            throws InvalidBookingException {
        purchaseAndVerifyBooking(
                "43.49",
                2L,
                new TicketRequest(TicketType.ADULT, 1),
                new TicketRequest(TicketType.CHILD, 1));
    }

    @Test
    void shouldNotReserveAnExtraSeatForAnInfant()
            throws InvalidBookingException {
        purchaseAndVerifyBooking(
                "25.99",
                1L,
                new TicketRequest(TicketType.ADULT, 1),
                new TicketRequest(TicketType.INFANT, 1));
    }

    @Test
    void shouldCalculatePaymentAndReserveSeatsForMultipleTicketTypes()
            throws InvalidBookingException {
        purchaseAndVerifyBooking(
                "104.48",
                5L,
                new TicketRequest(TicketType.ADULT, 2),
                new TicketRequest(TicketType.CHILD, 3),
                new TicketRequest(TicketType.INFANT, 2));
    }

    @Test
    void shouldNotCallPaymentOrReserveSeatsWhenBookingIsInvalid() {
        TicketRequest child = new TicketRequest(TicketType.CHILD, 1);

        assertThrows(
                InvalidBookingException.class,
                () -> cinemaTicketsService.purchaseTickets(123L, child));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    private void purchaseAndVerifyBooking(
            String expectedAmount,
            Long expectedSeats,
            TicketRequest... ticketRequests)
            throws InvalidBookingException {

        when(paymentService.debitAccount(
                anyLong(), any(BigDecimal.class)))
                .thenReturn(ResponseEntity.ok("Payment successful"));

        when(seatReservationService.reserveSeats(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok("Seats reserved"));

        BookingConfirmation confirmation =
                cinemaTicketsService.purchaseTickets(123L, ticketRequests);

        assertEquals(123L, confirmation.accountId());

        verify(paymentService).debitAccount(
                eq(123L),
                eq(new BigDecimal(expectedAmount)));

        verify(seatReservationService).reserveSeats(
                eq(123L),
                eq(expectedSeats));
    }
}
