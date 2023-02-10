package uz.pdp.comunicationsystem.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Owner implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String username;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @Column(name = "turniket", updatable = false)
    private String turniket = UUID.randomUUID().toString();
    @Column(name = "is_account_non_expired")
    private boolean isAccountNonExpired = true;
    @Column(name = "is_account_non_locked")
    private boolean isAccountNonLocked = true;
    @Column(name = "is_credentials_non_expired")
    private boolean isCredentialsNonExpired = true;
    @Column(name = "is_enabled")
    private boolean isEnabled = false;
    @ManyToMany
    @ToString.Exclude
    private Set<Role> roles;
    @ManyToOne
    private Filial filial;

    public Owner(String username, String firstName, String lastName, String password, boolean isEnabled, Set<Role> roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isEnabled = isEnabled;
        this.roles = roles;
    }

    public Owner(String username, String firstName, String lastName, String password, boolean isEnabled, Set<Role> roles, Filial filial) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.isEnabled = isEnabled;
        this.roles = roles;
        this.filial = filial;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Owner owner = (Owner) o;
        return id != null && Objects.equals(id, owner.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
