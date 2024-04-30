package com.attus.testetecnico.controllers.address;

import com.attus.testetecnico.controllers.address.converter.AddressEntityToResponseBodyConverter;
import com.attus.testetecnico.controllers.address.converter.AddressRequestBodyToEntityConverter;
import com.attus.testetecnico.controllers.address.dto.AddressRequestBody;
import com.attus.testetecnico.services.AddressService;
import com.attus.testetecnico.system.HttpResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("${api.endpoint.base-url}/address")
public class AddressController {

    private final AddressService addressService;

    private final AddressRequestBodyToEntityConverter addressRequestBodyToEntityConverter;

    private final AddressEntityToResponseBodyConverter addressEntityToResponseBodyConverter;

    public AddressController(AddressService addressService, AddressRequestBodyToEntityConverter addressRequestBodyToEntityConverter, AddressEntityToResponseBodyConverter addressEntityToResponseBodyConverter) {
        this.addressService = addressService;
        this.addressRequestBodyToEntityConverter = addressRequestBodyToEntityConverter;
        this.addressEntityToResponseBodyConverter = addressEntityToResponseBodyConverter;
    }

    @PostMapping("/person/{personId}")
    public ResponseEntity<HttpResponseResult> createNewAddress(@PathVariable("personId") Long personId,
                                                               @RequestBody @Validated AddressRequestBody requestBody) {
        var address = this.addressService.create(personId,
                Objects.requireNonNull(this.addressRequestBodyToEntityConverter.convert(requestBody)));

        var responseAddress = this.addressEntityToResponseBodyConverter.convert(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new HttpResponseResult(
                        true,
                        "Created address success",
                        LocalDateTime.now(),
                        responseAddress
                )
        );
    }
}
