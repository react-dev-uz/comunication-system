package uz.pdp.comunicationsystem.payload.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.comunicationsystem.entity.enums.PaymentType;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class PayType {
    private Integer id;
    @Enumerated(EnumType.STRING)
    private PaymentType name;
}
