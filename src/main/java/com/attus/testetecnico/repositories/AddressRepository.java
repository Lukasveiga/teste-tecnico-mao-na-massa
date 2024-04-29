package com.attus.testetecnico.repositories;

import com.attus.testetecnico.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
