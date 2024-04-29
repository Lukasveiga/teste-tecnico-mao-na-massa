package com.attus.testetecnico.system;

import java.time.LocalDateTime;

public record HttpResponseResult(boolean flag, String message, LocalDateTime dateTime, Object data) {
}
