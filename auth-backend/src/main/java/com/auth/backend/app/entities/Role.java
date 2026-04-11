package com.auth.backend.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
@Table(name = "roles")
public class Role {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(unique = true, nullable = false)
    private String name;
}
