package uz.pdp.comunicationsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "sim_card", uniqueConstraints = {@UniqueConstraint(columnNames = {"code", "number"})})
public class SimCard implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(name = "code")
    private String code;

    @Column(name = "number")
    private String number;

    @Column(name = "card_pin_code")
    private String cardPinCode = (Math.random() * 10000) + "";

    @Column(name = "pin_code_number")
    private String pinCode;

    private boolean active;
    private double balance = 0;
    private double amountMb = 0;
    private double amountSMS = 0;
    private double amountMinute = 0;

    private boolean isDebit = false;

    @Transient
    private String fullNumber;

    public String getFullNumber() {
        return String.format("998%s", (code + number));
    }

    @ManyToOne
    private Tariff tariff;

    @ManyToOne
    private Client client;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public SimCard(String name, String code, String number, boolean active, double balance, double amountMb, double amountSMS, double amountMinute, Tariff tariff, Client client) {
        this.name = name;
        this.code = code;
        this.number = number;
        this.active = active;
        this.balance = balance;
        this.amountMb = amountMb;
        this.amountSMS = amountSMS;
        this.amountMinute = amountMinute;
        this.tariff = tariff;
        this.client = client;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return client.getRoles();
    }

    @Override
    public String getPassword() {
        return this.pinCode;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SimCard simCard = (SimCard) o;
        return id != null && Objects.equals(id, simCard.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
