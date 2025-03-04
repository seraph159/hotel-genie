package com.jeff.hotel_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CreditCard")
public class CreditCard {

    @Id
    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String holdername;

    @Column(nullable = false, length = 16)
    private String cardnumber;

    @Column(nullable = false, length = 20)
    private String type;

    @OneToOne
    @MapsId
    @JoinColumn(name = "email")
    private Client client;

    // Getters and Setters
}
