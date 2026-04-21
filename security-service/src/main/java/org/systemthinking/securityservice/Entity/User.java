package org.systemthinking.securityservice.Entity;
// WHAT: Package name (folder structure)
// WHY: Organizes code properly




import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// WHAT: Used in JSON conversion
// WHY: Prevents infinite loop while converting objects to JSON

import jakarta.persistence.*;
// WHAT: JPA annotations (Entity, Table, Id etc.)
// WHY: Used to map class → database table

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// WHAT: Validation annotations
// WHY: Ensure correct data before saving to DB

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// WHAT: Lombok annotations
// WHY: Automatically generates getters, setters, constructors

import javax.management.relation.Role;
// ❌ NOTE: This is WRONG import (very important)
// WHY: Should be your own Role entity, not this one

import java.util.HashSet;
import java.util.Set;
// WHAT: Collection classes
// WHY: Used to store roles

@Entity
// WHAT: Marks this class as DB entity
// WHY: Tells Spring → create table for this class

@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
// WHAT: Table configuration
// WHY:
// 1. Table name = users
// 2. username & email must be unique (no duplicates)

@Data
// WHAT: Lombok → generates getters/setters, toString
// WHY: Avoid writing boilerplate code

@AllArgsConstructor
// WHAT: Creates constructor with all fields
// WHY: Useful for object creation

@NoArgsConstructor
// WHAT: Creates empty constructor
// WHY: Required by JPA

public class User {

    @Id
    // WHAT: Primary key
    // WHY: Uniquely identifies each user

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // WHAT: Auto-increment ID
    // WHY: DB automatically generates ID

    private Long id;


    @NotBlank(message = "Username is required")
    // WHAT: Cannot be null or empty
    // WHY: User must enter username

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    // WHAT: Length validation
    // WHY: Prevent too short/long usernames

    private String username;


    @NotBlank(message = "Email is required")
    // WHAT: Email cannot be empty

    @Size(max = 100)
    // WHAT: Max length 100

    @Email(message = "Email must be valid")
    // WHAT: Must follow email format

    @Column(unique = true, nullable = false, length = 100)
    // WHAT: DB column config
    // WHY:
    // 1. Unique → no duplicate emails
    // 2. Not null → must exist
    // 3. Max length 100

    private String email;


    @NotBlank(message = "Password is required")
    // WHAT: Password cannot be empty

    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    // WHAT: Password length restriction

    @Column(nullable = false, length = 120)
    // WHAT: DB column config
    // WHY: Password must exist and limited size

    private String password;


    @ManyToMany(fetch = FetchType.LAZY)
    // WHAT: Many-to-Many relationship
    // WHY:
    // 1 user → many roles
    // 1 role → many users

    @JoinTable(
            name = "user_roles",
            // WHAT: Join table name
            // WHY: Needed to store relation

            joinColumns = @JoinColumn(name = "user_id"),
            // WHAT: FK for user

            inverseJoinColumns = @JoinColumn(name = "role_id")
            // WHAT: FK for role
    )

    @JsonIgnoreProperties("users")
    // WHAT: Ignore "users" field in Role while converting to JSON
    // WHY: Prevent infinite loop (User → Role → User → Role...)

    private Set<Role> roles = new HashSet<>();
    // WHAT: Stores roles
    // WHY: Each user can have multiple roles


// OPTIONAL METHODS (COMMENTED)

//    public void addRole(Role role){
//        this.roles.add(role);
//        role.getUsers().add(this);
//    }
// WHY: Add role to user and maintain both sides

//  public void removeRole(Role role){
//       this.roles.remove(role);
//        role.getUsers().remove(this);
////    }
//// WHY: Remove role safely from both sides
}