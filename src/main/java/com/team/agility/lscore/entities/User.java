package com.team.agility.lscore.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private String firstname;
    private String lastname;
    
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @Builder.Default
    @Column(columnDefinition = "integer default 0")
    private Integer invalidAttempts = 0;

    @Builder.Default
    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    private Boolean locked = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "users_id"),
        inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private final Set<Role> roles = new HashSet<>();
}
