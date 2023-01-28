package uz.pdp.comunicationsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uz.pdp.comunicationsystem.payload.response.EntertainmentServiceDTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "entertainment_service")
public class Entertainment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(name = "duration_activity_day", nullable = false)
    private Integer durationDay;

    @Column(name = "expire_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToMany
    @ToString.Exclude
    private List<SimCard> simCards = new ArrayList<>();

    public Entertainment(EntertainmentServiceDTO dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.durationDay = dto.getDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Entertainment that = (Entertainment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
