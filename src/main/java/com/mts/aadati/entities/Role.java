package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "role",
        indexes = @Index(name = "inx_role_name", columnList = "name")
)
public class Role {

    @EqualsAndHashCode.Include
    @Id
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id", nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID roleId;

    @ToString.Include
    @Column(name = "name", length = 50, nullable = false , unique = true)
    @Size(max = 50, min = 3, message = "Name must be between 3 and 50 characters")
    @NotBlank(message = "Name is required")
    @Setter
    @NaturalId
    private String name;

    @ToString.Include
    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "Description must be between 0 and 1000 characters")
    @Setter
    private String description;

    @ToString.Include
    @Column(name = "updated_at" , nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    @ToString.Include
    @Column(name = "created_at" , nullable = false , updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @ToString.Include
    @Column(name = "is_deleted", nullable = false)
    @Setter
    @JsonIgnore
    private boolean isDeleted;


    // ===== RelationShip =====
    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    private final List<User> users =new ArrayList<>();

    // ===== Builder Constructor =====
    @Builder public Role(@NonNull String name, String description) {
        this.name = name;
        this.description = description;
        this.isDeleted = false;
    }

    // ===== Lifecycle Callback ======
    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        updatedAt = now ;
        createdAt = now ;
    }
    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }
}
