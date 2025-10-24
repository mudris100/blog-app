package dev.mudris.dto;

public record UserDto(Integer id, String username, String email, dev.mudris.entity.Role role) {

}
