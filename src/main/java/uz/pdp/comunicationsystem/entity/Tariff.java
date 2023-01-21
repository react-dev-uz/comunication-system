package uz.pdp.comunicationsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import uz.pdp.comunicationsystem.entity.enums.ClientType;
import uz.pdp.comunicationsystem.payload.request.TariffDTO;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false, name = "transfer_price")
    private Double transferPrice;
    @Column(name = "expire_date")
    private Date expireDate;
    @Column(nullable = false)
    private Integer mb;
    @Column(nullable = false)
    private Integer sms;
    @Column(nullable = false)
    private Integer min;
    private Double mbCost;
    private Double smsCost;
    private Double minCost;
    @Column(name = "client_type")
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    public Tariff(Long tariffId, TariffDTO dto) {
        if (tariffId!=null) this.id = tariffId;
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.transferPrice = dto.getTransferPrice();
        this.expireDate = dto.getExpireDate();
        this.mb = dto.getMb();
        this.sms = dto.getSms();
        this.min = dto.getMin();
        this.mbCost = dto.getMbCost();
        this.smsCost = dto.getSmsCost();
        this.minCost = dto.getMinCost();
        this.clientType = dto.isCompany() ? ClientType.COMPANY : ClientType.USER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Tariff tariff = (Tariff) o;
        return id != null && Objects.equals(id, tariff.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
