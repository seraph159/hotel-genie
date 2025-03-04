package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.BookingDto;
import com.jeff.hotel_management_system.entity.*;
import com.jeff.hotel_management_system.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private OccupiesRepository occupiesRepository;

    @Mock
    private PricingService pricingService;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private Client client;
    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client("1234567890", PaymentType.CREDIT_CARD);
        client.setName("John Doe");
        client.setEmail("client@email.com");
        client.setPassword("password");
        client.setRole("CLIENT");
        room = new Room("101", 1, 2, true, 100L, "Single", true, true, true, true, true, "Pool Access", 4.5, "Family");
        booking = new Booking(new BookingId(LocalDate.now(), "101"), 200L, client, room, LocalDate.now().plusDays(3));
    }

    @Test
    void testGetAllBookings() {
        Booking booking2 = new Booking(new BookingId(LocalDate.now().plusDays(1), "102"), 300L, client, room, LocalDate.now().plusDays(4));
        given(bookingRepository.findAll()).willReturn(Arrays.asList(booking, booking2));

        List<BookingDto> bookings = bookingService.getAllBookings();

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getPrice()).isEqualTo(200L);
        assertThat(bookings.get(1).getPrice()).isEqualTo(300L);
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testGetBookingById() {
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));

        Optional<Booking> result = bookingService.getBookingById(booking.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualTo(200L);
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void testFindByClientEmail() {
        given(bookingRepository.findByClientEmail("client@email.com")).willReturn(Arrays.asList(booking));

        List<Booking> bookings = bookingService.findByClientEmail("client@email.com");

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getClient().getEmail()).isEqualTo("client@email.com");
        verify(bookingRepository, times(1)).findByClientEmail("client@email.com");
    }

    @Test
    void testCreateBooking() {
        given(clientRepository.findByEmail("client@email.com")).willReturn(Optional.of(client));
        given(roomRepository.findById("101")).willReturn(Optional.of(room));
        given(bookingRepository.save(booking)).willReturn(booking);
        given(occupiesRepository.save(any(Occupies.class))).willReturn(new Occupies(new OccupiesId("client@email.com", booking.getId().getStartDate(), booking.getId().getRoomNr()), client, booking));

        Booking result = bookingService.createBooking(booking, "client@email.com", "101");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        verify(clientRepository, times(1)).findByEmail("client@email.com");
        verify(roomRepository, times(1)).findById("101");
        verify(bookingRepository, times(1)).save(booking);
        verify(occupiesRepository, times(1)).save(any());
    }

    @Test
    void testCreateBookingAdmin() {
        BookingDto bookingDto = new BookingDto(200L, LocalDate.now(), LocalDate.now().plusDays(3), "101", "client@email.com", room);
        given(roomRepository.findById("101")).willReturn(Optional.of(room));
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));
        given(pricingService.calculatePrice("101", LocalDate.now(), LocalDate.now().plusDays(3))).willReturn(200L);
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);

        Booking result = bookingService.createBookingAdmin(bookingDto);

        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(200L);
        verify(roomRepository, times(1)).findById("101");
        verify(clientRepository, times(1)).findById("client@email.com");
        verify(pricingService, times(1)).calculatePrice("101", LocalDate.now(), LocalDate.now().plusDays(3));
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void testUpdateBooking() {
        BookingDto bookingDto = new BookingDto(200L, LocalDate.now(), LocalDate.now().plusDays(4), "101", "client@email.com", room);
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        given(pricingService.calculatePrice("101", LocalDate.now(), LocalDate.now().plusDays(4))).willReturn(250L);
        given(bookingRepository.save(booking)).willReturn(booking);

        Booking result = bookingService.updateBooking(bookingDto);

        assertThat(result).isNotNull();
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusDays(4));
        assertThat(result.getPrice()).isEqualTo(250L);
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(pricingService, times(1)).calculatePrice("101", LocalDate.now(), LocalDate.now().plusDays(4));
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void testDeleteBooking() {
        // Mock authentication
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn("client@email.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Mock repositories
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
        Occupies occupies = new Occupies(
                new OccupiesId("client@email.com", booking.getId().getStartDate(), booking.getId().getRoomNr()),
                client, booking
        );
        given(occupiesRepository.findById(any())).willReturn(Optional.of(occupies));

        // Execute the service method
        boolean result = bookingService.deleteBooking(booking.getId());

        // Verify results
        assertThat(result).isTrue();
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(occupiesRepository, times(1)).findById(any());
        verify(occupiesRepository, times(1)).delete(occupies);
        verify(bookingRepository, times(1)).delete(booking);
    }


    @Test
    void testDeleteBookingAdmin() {
        given(bookingRepository.existsById(booking.getId())).willReturn(true);

        boolean result = bookingService.deleteBookingAdmin(booking.getId());

        assertThat(result).isTrue();
        verify(bookingRepository, times(1)).existsById(booking.getId());
        verify(bookingRepository, times(1)).deleteById(booking.getId());
    }

    @Test
    void testDeleteBookingAdmin_BookingNotFound() {
        given(bookingRepository.existsById(booking.getId())).willReturn(false);

        boolean result = bookingService.deleteBookingAdmin(booking.getId());

        assertThat(result).isFalse();
        verify(bookingRepository, times(1)).existsById(booking.getId());
        verify(bookingRepository, never()).deleteById(any());
    }
}