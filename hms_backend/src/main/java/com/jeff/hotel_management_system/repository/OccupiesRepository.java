package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.Occupies;
import com.jeff.hotel_management_system.entity.OccupiesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupiesRepository extends JpaRepository<Occupies, OccupiesId> {
    // OccupiesId corresponds to the type of the composite primary key
}