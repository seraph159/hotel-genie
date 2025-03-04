package com.jeff.hotel_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Admin")
public class Admin extends User {
    // Additional fields specific to Admin, if any
    @Column(length = 20)
    private String phone;
}
