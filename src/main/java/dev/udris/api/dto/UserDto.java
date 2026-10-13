package dev.udris.api.dto;

public record UserDto(Integer id, String username, String email, dev.udris.entity.Role role) {

}
