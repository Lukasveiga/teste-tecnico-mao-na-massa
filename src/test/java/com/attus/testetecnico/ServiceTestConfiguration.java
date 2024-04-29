package com.attus.testetecnico;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@Tag("Service")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public interface ServiceTestConfiguration {
}
