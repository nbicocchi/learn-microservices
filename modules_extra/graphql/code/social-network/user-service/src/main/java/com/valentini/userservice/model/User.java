package com.valentini.userservice.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    @Column
    private String password;
    @Column
    private String avatarPath;

}
