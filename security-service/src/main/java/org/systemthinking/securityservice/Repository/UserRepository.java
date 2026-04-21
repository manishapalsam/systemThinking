package org.systemthinking.securityservice.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.systemthinking.securityservice.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);


    @Query("Select DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
