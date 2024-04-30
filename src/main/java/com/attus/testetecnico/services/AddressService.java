package com.attus.testetecnico.services;

import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    private final PersonService personService;

    public AddressService(AddressRepository addressRepository, PersonService personService) {
        this.addressRepository = addressRepository;
        this.personService = personService;
    }

    @Transactional
    public Address create(Long personId, Address address) {
        var person = this.personService.findOne(personId);
        address.setPerson(person);
        return this.addressRepository.save(address);
    }
}
