package dev.udris.api.errors;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String title, String detail, LocalDateTime timestamp){

}
