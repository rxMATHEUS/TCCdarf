package org.api.darf.repositories;

import org.api.darf.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByCpf(String cpf);
}
