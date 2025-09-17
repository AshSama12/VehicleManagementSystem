package com.company.vehiclemanagement.repository;

import com.company.vehiclemanagement.model.User;
import com.company.vehiclemanagement.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    List<User> findByRole(Role role);

    List<User> findByActiveTrue();

    long countByActiveTrue();

    List<User> findByDepartment(String department);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);
}