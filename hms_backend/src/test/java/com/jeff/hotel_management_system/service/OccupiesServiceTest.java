package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.*;
import com.jeff.hotel_management_system.repository.OccupiesRepository;
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

class OccupiesServiceTest {

    @Mock
    private OccupiesRepository occupiesRepository;

    @InjectMocks
    private OccupiesService occupiesService;

    private OccupiesId occupiesId;
    private Occupies occupies;
    private Client client;
    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client("1234567890", PaymentType.CREDIT_CARD);
        client.setEmail("client@email.com");
        client.setPassword("password");
        client.setRole("CLIENT");

        booking = new Booking(new BookingId(LocalDate.now(), "101"), 100L, client, new Room(), LocalDate.now().plusDays(3));
        occupiesId = new OccupiesId("client@email.com", LocalDate.now(), "101");
        occupies = new Occupies(occupiesId, client, booking);
    }

    @Test
    void testGetAllOccupies() {
        Occupies occupies1 = new Occupies(new OccupiesId("client1@email.com", LocalDate.now(), "101"), client, booking);
        Occupies occupies2 = new Occupies(new OccupiesId("client2@email.com", LocalDate.now(), "102"), client, booking);

        given(occupiesRepository.findAll()).willReturn(Arrays.asList(occupies1, occupies2));

        List<Occupies> occupiesList = occupiesService.getAllOccupies();

        assertThat(occupiesList).hasSize(2);
        verify(occupiesRepository, times(1)).findAll();
    }

    @Test
    void testGetOccupiesById() {
        given(occupiesRepository.findById(occupiesId)).willReturn(Optional.of(occupies));

        Optional<Occupies> result = occupiesService.getOccupiesById(occupiesId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(occupiesId);
        verify(occupiesRepository, times(1)).findById(occupiesId);
    }

    @Test
    void testCreateOccupies() {
        given(occupiesRepository.save(occupies)).willReturn(occupies);

        Occupies result = occupiesService.createOccupies(occupies);

        assertThat(result.getId()).isEqualTo(occupiesId);
        verify(occupiesRepository, times(1)).save(occupies);
    }

    @Test
    void testDeleteOccupies() {
        given(occupiesRepository.findById(occupiesId)).willReturn(Optional.of(occupies));

        boolean result = occupiesService.deleteOccupies(occupiesId);

        assertThat(result).isTrue();
        verify(occupiesRepository, times(1)).findById(occupiesId);
        verify(occupiesRepository, times(1)).delete(occupies);
    }

    @Test
    void testDeleteOccupies_NotFound() {
        given(occupiesRepository.findById(occupiesId)).willReturn(Optional.empty());

        boolean result = occupiesService.deleteOccupies(occupiesId);

        assertThat(result).isFalse();
        verify(occupiesRepository, times(1)).findById(occupiesId);
        verify(occupiesRepository, never()).delete(any());
    }
}