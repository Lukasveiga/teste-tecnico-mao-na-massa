package com.attus.testetecnico.services;

import com.attus.testetecnico.entities.Address;
import com.attus.testetecnico.repositories.AddressRepository;
import com.attus.testetecnico.services.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Address findOne(Long personId, Long addressId) {
        return this.personService.findOne(personId).getAddresses()
                .stream()
                .filter((a) -> a.getId().equals(addressId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Address with id %d was not found".formatted(addressId)));
    }

    public List<Address> findAll(Long personId) {
        return this.addressRepository.findAllByPersonId(personId);
    }

    public List<Address> findAllPageable(Long personId, int page) {
        var size = 5;
        var pageable = PageRequest.of(page, size);
        var pageAddress = this.addressRepository.findAllByPersonId(personId, pageable);
        return pageAddress.getContent();
    }

    @Transactional
    public Address update(Long personId, Long id, Address address) {
        var oldAddress = this.findOne(personId, id);

        updateAddress(address, oldAddress);
        return this.addressRepository.save(oldAddress);
    }

    private static void updateAddress(Address address, Address oldAddress) {
        oldAddress.setStreet(address.getStreet());
        oldAddress.setZipCode(address.getZipCode());
        oldAddress.setNumber(address.getNumber());
        oldAddress.setCity(address.getCity());
        oldAddress.setState(address.getState());
        oldAddress.setMain(address.isMain());
    }
}
