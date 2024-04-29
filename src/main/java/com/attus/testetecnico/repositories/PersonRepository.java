package com.attus.testetecnico.repositories;

import com.attus.testetecnico.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
