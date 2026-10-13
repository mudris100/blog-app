package dev.udris.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.udris.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByIdNotNull();

    List<User> findByUsernameIn(Collection<String> usernames);
	Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User >findByProviderAndUsername(String provider, String username);
}
