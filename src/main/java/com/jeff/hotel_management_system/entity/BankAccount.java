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
@Table(name = "BankAccount")
public class BankAccount {

    @Id
    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String bank;

    @Column(nullable = false, length = 20)
    private String accountnumber;

    @Column(nullable = false, length = 20)
    private String routingnumber;

    @OneToOne
    @MapsId
    @JoinColumn(name = "email")
    private Client client;

    // Getters and Setters
}
