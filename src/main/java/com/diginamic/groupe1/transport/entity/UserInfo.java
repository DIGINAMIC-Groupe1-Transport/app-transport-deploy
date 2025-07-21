package com.diginamic.groupe1.transport.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entité représentant un utilisateur du système
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "corp_email")
    private String corpEmail;

    @Column(name = "personal_email")
    private String personalEmail;

    @Column(name="password")
    private String password;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "userInfo")
    private Set<Vehicle> vehicles = new HashSet<>();

    @OneToMany(mappedBy = "organizer")
    private Set<Carpool> organizedCarpools = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "USERS_CARPOOLS_PARTICIPANTS",
            joinColumns = @JoinColumn(name = "fk_user_participant_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_carpool_id")
    )
    private Set<Carpool> participatedCarpools = new HashSet<>();

    @OneToMany(mappedBy = "userInfo")
    private Set<Reservation> reservations = new HashSet<>();

    @Column(name="is_enabled")
    private Boolean isEnabled;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "MESSAGES_USERS",
            joinColumns = @JoinColumn(name = "fk_user_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_message_id"))
    private Set<Message> messages;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ROLES_USERS",
            joinColumns = @JoinColumn(name = "fk_user_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_role_id"))
    private Set<Role> roles;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof UserInfo userInfo) {
            return userInfo.getCorpEmail().equals(getCorpEmail());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(corpEmail);
    }
}
