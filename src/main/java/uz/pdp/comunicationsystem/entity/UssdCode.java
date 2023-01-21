package uz.pdp.comunicationsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import javax.management.ConstructorParameters;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class UssdCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull
    private String description;

    public UssdCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UssdCode ussdCode = (UssdCode) o;
        return id != null && Objects.equals(id, ussdCode.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
