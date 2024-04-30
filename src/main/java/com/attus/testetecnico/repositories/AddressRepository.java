package com.attus.testetecnico.repositories;

import com.attus.testetecnico.entities.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = "SELECT address.* FROM address WHERE address.person_id = ?1", nativeQuery = true)
    List<Address> findAllByPersonId(Long personId);

    @Query(value = "SELECT address.* FROM address WHERE address.person_id = ?1", nativeQuery = true)
    Page<Address> findAllByPersonId(Long personId, Pageable pageable);

}
