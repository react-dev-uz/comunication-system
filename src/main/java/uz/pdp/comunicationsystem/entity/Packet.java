package uz.pdp.comunicationsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import uz.pdp.comunicationsystem.entity.enums.PacketType;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table
public class Packet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private Integer amount; // Miqdori
    private Double price;
    private Integer activeTime; // aktivligi kun hisobida

    @Column(name = "packet_type")
    @Enumerated(EnumType.STRING)
    private PacketType packetType;

    @OneToOne(optional = false)
    private UssdCode ussdCode;

    @ManyToMany
    @JsonIgnore
    @ToString.Exclude
    private List<SimCard> simCards;     // Xizmatga ulangan sim kartalar

    @OneToMany
    @ToString.Exclude
    private Set<Tariff> accessTariff; //    ruxsat etilganlar

    public Packet(String name, Integer amount, Double price, Integer activeTime, PacketType packetType, UssdCode ussdCode, Set<Tariff> accessTariff) {
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.activeTime = activeTime;
        this.packetType = packetType;
        this.ussdCode = ussdCode;
        this.accessTariff = accessTariff;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Packet packet = (Packet) o;
        return id != null && Objects.equals(id, packet.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
