package com.attus.testetecnico.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String fullName;

    private LocalDate dateOfBirth;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    @JsonManagedReference
    private final List<Address> addresses = new ArrayList<>();

    public void addAddresses(Address address) {
        this.addresses.add(address);
    }

    public Address getMainAddress() {
        return this.addresses.stream().filter(Address::isMain).findFirst().orElseGet(Address::new);
    }
}
