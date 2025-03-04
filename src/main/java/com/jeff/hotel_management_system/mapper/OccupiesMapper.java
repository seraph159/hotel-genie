package com.jeff.hotel_management_system.mapper;


import com.jeff.hotel_management_system.dto.OccupiesDto;
import com.jeff.hotel_management_system.entity.Booking;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.entity.Occupies;
import com.jeff.hotel_management_system.entity.OccupiesId;
import org.springframework.stereotype.Component;

@Component
public class OccupiesMapper {

    public OccupiesDto toDTO(Occupies occupies) {
        return new OccupiesDto(
                occupies.getId().getClientEmail(),
                occupies.getId().getBookingStartDate(),
                occupies.getId().getBookingRoomNr()
        );
    }

    public Occupies toEntity(OccupiesDto OccupiesDto, Client client, Booking booking) {
        OccupiesId occupiesId = new OccupiesId(
                OccupiesDto.getClientEmail(),
                OccupiesDto.getBookingStartDate(),
                OccupiesDto.getBookingRoomNr()
        );

        Occupies occupies = new Occupies();
        occupies.setId(occupiesId);
        occupies.setClient(client);
        occupies.setBooking(booking);

        return occupies;
    }
}