package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Room;
import com.jeff.hotel_management_system.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PricingServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculatePrice_ValidRoomNumber() {
        String roomNr = "101";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 5);
        Room room = new Room();
        room.setRoomNr(roomNr);
        room.setBasePrice(100L);

        given(roomRepository.findByRoomNr(roomNr)).willReturn(Optional.of(room));

        Long price = pricingService.calculatePrice(roomNr, startDate, endDate);

        assertThat(price).isEqualTo(400L); // (5 - 1) * 100
        verify(roomRepository, times(1)).findByRoomNr(roomNr);
    }

    @Test
    void testCalculatePrice_InvalidRoomNumber() {
        String roomNr = "999";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 5);

        given(roomRepository.findByRoomNr(roomNr)).willReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                pricingService.calculatePrice(roomNr, startDate, endDate));

        assertThat(exception.getMessage()).isEqualTo("Room not found");
        verify(roomRepository, times(1)).findByRoomNr(roomNr);
    }

    @Test
    void testCalculatePrice_WithRoomEntity() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 5);
        Room room = new Room();
        room.setRoomNr("102");
        room.setBasePrice(200L);

        Long price = pricingService.calculatePrice(room, startDate, endDate);

        assertThat(price).isEqualTo(800L); // (5 - 1) * 200
    }

    @Test
    void testCalculatePrice_ZeroDaysStay() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 1);
        Room room = new Room();
        room.setRoomNr("103");
        room.setBasePrice(150L);

        Long price = pricingService.calculatePrice(room, startDate, endDate);

        assertThat(price).isEqualTo(0L); // No days between start and end
    }

    @Test
    void testCalculatePrice_NegativeDaysStay() {
        LocalDate startDate = LocalDate.of(2023, 1, 5);
        LocalDate endDate = LocalDate.of(2023, 1, 1);
        Room room = new Room();
        room.setRoomNr("104");
        room.setBasePrice(100L);

        // Assuming the method doesn't handle negative days and it's a caller responsibility
        Long price = pricingService.calculatePrice(room, startDate, endDate);

        assertThat(price).isEqualTo(-400L); // (1 - 5) * 100
    }
}
