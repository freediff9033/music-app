package org.acme; // 1. Check: Is there a semicolon at the end?

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends PanacheEntity { // 2. Check: Is 'User' capitalized here?
    
    public String username;
    public String password;

    // 3. Check: Is the return type 'User' capitalized here?
    public static User findByName(String username) {
        return find("username", username).firstResult();
    }
}