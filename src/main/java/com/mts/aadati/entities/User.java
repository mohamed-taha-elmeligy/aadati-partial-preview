
package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.Instant;
import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter @ToString(exclude = {"password","habits","habitTasks","roles","percentageDays"}) @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "users" , indexes = {
        @Index(name = "inx_user_first_name" , columnList = "first_name"),
        @Index(name = "inx_user_last_name" , columnList = "last_name"),
        @Index(name = "inx_user_email" , columnList = "email")
})
public class User {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID )
    @Column(name = "user_id" , nullable = false ,updatable = false , columnDefinition = "UUID")
    private UUID userId ;

    @Column(name = "first_name", nullable = false, length = 30)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$", message = "Name can only contain letters, spaces, dots, hyphens and apostrophes")
    @Setter
    private String firstName ;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$", message = "Name can only contain letters, spaces, dots, hyphens and apostrophes")
    @Setter
    private String lastName ;

    @Column(name = "username", nullable = false, unique = true, length = 30)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Setter
    private String username ;

    @Column(name = "password", nullable = false, length = 200)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters")
    @JsonIgnore
    @Setter
    private String password ;

    @Column(name = "email", nullable = false, unique = true, length = 254) // RFC standard
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(min = 5, max = 254, message = "Email must be between 5 and 254 characters")
    @Setter
    private String email ;

    @Column(name = "email_verified" , nullable = false)
    @Setter
    private boolean emailVerified ;

    @Column(name = "updated_at" , nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    @Column(name = "created_at" , nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    // ===== Relationship =====
    @JsonIgnore
    @OneToMany(mappedBy = "user" ,fetch = FetchType.LAZY ,cascade = CascadeType.ALL )
    private final List<Habit> habits = new ArrayList<>() ;

    @JsonIgnore
    @OneToMany(mappedBy = "user" ,fetch = FetchType.LAZY ,cascade = CascadeType.ALL )
    private final List<HabitTask> habitTasks = new ArrayList<>() ;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PercentageDay> percentageDays = new ArrayList<>();

    @Setter
    @ManyToMany(cascade ={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH}, fetch = FetchType.EAGER )
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id" ,nullable = false),
            inverseJoinColumns = @JoinColumn(name ="role_id",nullable = false ))
    private List<Role> roles = new ArrayList<>();



    // ===== Constructor =====
    @Builder (access = AccessLevel.PUBLIC)
    public User (@NonNull String firstName,@NonNull String lastName ,@NonNull String username ,@NonNull String password ,@NonNull String email ,boolean emailVerified ){
        this.firstName = firstName ;
        this.lastName = lastName ;
        this.username = username ;
        this.password = password ;
        this.email = email ;
        this.emailVerified = emailVerified  ;
    }

    // ===== Lifecycle =====
    @PreUpdate
    private void perUpdates (){
        updatedAt = Instant.now();
    }
    @PrePersist
    private void perPersist (){
        createdAt = Instant.now();
        updatedAt = createdAt ;
    }

    // ===== Helper Method for Email verified =====
    @JsonIgnore
    public String getEmailVerifiedText(){
        return emailVerified ? ("Valid email "+email) : ("Invalid email "+email) ;
    }
    @JsonIgnore
    public boolean isValidEmail(){
        return emailVerified ;
    }
    public User invalidateEmail(){
        emailVerified = false ;
        return this ;
    }
    public User validateEmail(){
        emailVerified = true ;
        return this ;
    }


}
