package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Occupies;
import com.jeff.hotel_management_system.entity.OccupiesId;
import com.jeff.hotel_management_system.repository.OccupiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OccupiesService {

    @Autowired
    private OccupiesRepository occupiesRepository;

    public List<Occupies> getAllOccupies() {

        return occupiesRepository.findAll();
    }

    public Optional<Occupies> getOccupiesById(OccupiesId occupiesId) {

        return occupiesRepository.findById(occupiesId);
    }

    public Occupies createOccupies(Occupies occupies) {

        return occupiesRepository.save(occupies);
    }

    public boolean deleteOccupies(OccupiesId occupiesId) {

        return occupiesRepository.findById(occupiesId).map(occupies -> {
            occupiesRepository.delete(occupies);
            return true;
        }).orElse(false);
    }
}
