package org.campus.campusradarbackend.repository;

import org.campus.campusradarbackend.model.Role;
import org.campus.campusradarbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByisEnabledFalse();
    Optional<Object> findByEmail(String email);

    List<User> findByisEnabledFalseAndRole(Role role);
}
