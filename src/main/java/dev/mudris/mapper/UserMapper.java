package dev.mudris.mapper;

import org.springframework.stereotype.Component;

import dev.mudris.dto.UserDto;
import dev.mudris.dto.UserRegistrationDto;
import dev.mudris.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        return user;
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
