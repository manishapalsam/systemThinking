package org.systemthinking.securityservice.Entity;
// WHAT: Package name
// WHY: Organizes code in proper folder

import com.fasterxml.jackson.annotation.JsonIgnore;
// WHAT: JSON annotation
// WHY: Prevents infinite loop during API response

import jakarta.persistence.*;
// WHAT: JPA annotations
// WHY: Used to map class → database table

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// WHAT: Lombok
// WHY: Auto-generate constructors, getters, setters

import java.util.HashSet;
import java.util.Set;
// WHAT: Collection classes
// WHY: To store multiple users

@Entity
// WHAT: Marks this class as database table
// WHY: Spring will create "roles" table

@Table(name = "roles")
// WHAT: Table name
// WHY: Explicit naming in DB

@Data
// WHAT: Lombok → getters/setters/toString
// WHY: Avoid boilerplate code

@NoArgsConstructor
// WHAT: Empty constructor
// WHY: Required by JPA

@AllArgsConstructor
// WHAT: Constructor with all fields
// WHY: Easy object creation

public class Role {

    @Id
    // WHAT: Primary key
    // WHY: Unique ID for each role

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // WHAT: Auto-increment ID
    // WHY: DB generates ID automatically

    private Long id;


    @Column(unique = true, nullable = false, length = 50)
    // WHAT: Column rules
    // WHY:
    // 1. Unique → no duplicate roles (e.g., ADMIN only once)
    // 2. Not null → must have value
    // 3. Max length 50

    private String name;



    @ManyToMany(mappedBy = "roles")
    // WHAT: Many-to-Many relationship
    // WHY:
    // This is the "inverse side"
    // (User owns the relationship)

    @JsonIgnore
    // WHAT: Ignore this field in JSON
    // WHY:
    // Prevent infinite loop:
    // User → Role → User → Role...

    private Set<User> users = new HashSet<>();
    // WHAT: Stores users having this role
    // WHY: One role can belong to many users


    public Role(String name){
        // WHAT: Custom constructor
        // WHY: Easy to create role with just name
        this.name = name;
    }

}