package com.agilemonkeys.syntheticapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;


@ToString
@NoArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name="customers")
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    private String name;

    @Column(nullable = false)
    @NotEmpty
    private String surname;

    @Column(nullable = false, unique = true)
    @NotEmpty
    private String customerId;

    @Column(name = "photo")
    private String photo;


    public Customer(Long id, String name, String surname, String customerId, String photo) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.customerId = customerId;
        this.photo = photo;
    }

    public Customer(String name, String surname, String customerId, String photo) {
        this.name = name;
        this.surname = surname;
        this.customerId = customerId;
        this.photo = photo;
    }

    public Customer(String name, String surname, String customerId) {
        this.name = name;
        this.surname = surname;
        this.customerId = customerId;
    }
}
