package pl.csl.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.csl.library.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
