package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.RoomDto;
import com.jeff.hotel_management_system.dto.RoomResponseDto;
import com.jeff.hotel_management_system.entity.Room;
import com.jeff.hotel_management_system.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRooms() {
        Room room1 = new Room("101", 1, 2, true, 100L, "Single", true, true, true, true, true, "Pool Access", 4.5, "Family");
        Room room2 = new Room("102", 2, 4, false, 200L, "Double", false, true, true, false, false, "Gym Access", 4.0, "Business");

        given(roomRepository.findAll()).willReturn(Arrays.asList(room1, room2));

        List<RoomResponseDto> rooms = roomService.getAllRooms();

        assertThat(rooms).hasSize(2);
        assertThat(rooms.get(0).getRoomNr()).isEqualTo("101");
        assertThat(rooms.get(1).getRoomNr()).isEqualTo("102");
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void testGetRoomByRoomNr() {
        Room room = new Room("101", 1, 2, true, 100L, "Single", true, true, true, true, true, "Pool Access", 4.5, "Family");

        given(roomRepository.findById("101")).willReturn(Optional.of(room));

        Optional<Room> result = roomService.getRoomByRoomNr("101");

        assertThat(result).isPresent();
        assertThat(result.get().getRoomNr()).isEqualTo("101");
        verify(roomRepository, times(1)).findById("101");
    }

    @Test
    void testCreateRoom() {
        Room room = new Room("101", 1, 2, true, 100L, "Single", true, true, true, true, true, "Pool Access", 4.5, "Family");

        given(roomRepository.save(room)).willReturn(room);

        Room result = roomService.createRoom(room);

        assertThat(result.getRoomNr()).isEqualTo("101");
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testUpdateRoom() {
        Room existingRoom = new Room("101", 1, 2, true, 100L, "Single", true, true, true, true, true, "Pool Access", 4.5, "Family");
        Room updatedRoom = new Room("101", 1, 3, false, 150L, "Double", false, false, true, false, false, "Gym Access", 4.0, "Business");

        given(roomRepository.findById("101")).willReturn(Optional.of(existingRoom));
        given(roomRepository.save(updatedRoom)).willReturn(updatedRoom);

        Optional<Room> result = roomService.updateRoom("101", updatedRoom);

        assertThat(result).isPresent();
        assertThat(result.get().getMaxOccupancy()).isEqualTo(3);
        assertThat(result.get().isAvailable()).isFalse();
        verify(roomRepository, times(1)).findById("101");
        verify(roomRepository, times(1)).save(updatedRoom);
    }

    @Test
    void testDeleteRoom() {
        Room room = new Room("101", 1, 2, true, 100L, "Single", true, true, true, true, true, "Pool Access", 4.5, "Family");

        given(roomRepository.findById("101")).willReturn(Optional.of(room));

        boolean result = roomService.deleteRoom("101");

        assertThat(result).isTrue();
        verify(roomRepository, times(1)).findById("101");
        verify(roomRepository, times(1)).delete(room);
    }

    @Test
    void testDeleteRoom_NotFound() {
        given(roomRepository.findById("999")).willReturn(Optional.empty());

        boolean result = roomService.deleteRoom("999");

        assertThat(result).isFalse();
        verify(roomRepository, times(1)).findById("999");
        verify(roomRepository, never()).delete(any());
    }

    @Test
    void testAvailableRooms() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 5);
        Room room1 = new Room("101", 1, 2, true, 100L, "Single", true, true, true, true, true, "Pool Access", 4.5, "Family");
        Room room2 = new Room("102", 2, 4, true, 200L, "Double", false, true, true, false, false, "Gym Access", 4.0, "Business");

        given(roomRepository.findAvailableRooms(startDate, endDate, 2)).willReturn(Arrays.asList(room1, room2));
        given(pricingService.calculatePrice(room1, startDate, endDate)).willReturn((long) 400.0);
        given(pricingService.calculatePrice(room2, startDate, endDate)).willReturn((long) 800.0);

        List<RoomDto> result = roomService.availableRooms(startDate, endDate, 2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRoom().getRoomNr()).isEqualTo("101");
        assertThat(result.get(0).getPrice()).isEqualTo(400.0);
        assertThat(result.get(1).getRoom().getRoomNr()).isEqualTo("102");
        assertThat(result.get(1).getPrice()).isEqualTo(800.0);
        verify(roomRepository, times(1)).findAvailableRooms(startDate, endDate, 2);
        verify(pricingService, times(2)).calculatePrice(any(Room.class), eq(startDate), eq(endDate));
    }
}
