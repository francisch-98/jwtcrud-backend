package com.jwt.crud.security.repository;

import com.jwt.crud.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByNameUser(String nameUser);
    boolean existsByNameUser(String nameUser);
    boolean existsByEmail(String email);

}
