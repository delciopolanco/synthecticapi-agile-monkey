package com.agilemonkeys.syntheticapi.entities;

import jakarta.persistence.*;
import lombok.*;


@ToString
@AllArgsConstructor
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
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "photo")
    private String photo;


    public Customer(String name, String surname, String photo) {
        this.name = name;
        this.surname = surname;
        this.photo = photo;
    }
}
