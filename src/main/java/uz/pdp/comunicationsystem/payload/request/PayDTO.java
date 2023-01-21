package uz.pdp.comunicationsystem.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PayDTO {
    @NotNull
    @Size(min = 2, max = 2)
    private String code;
    @NotNull
    @Size(min = 7, max = 7)
    private String number;
    @NotNull
    @Min(0)
    private Integer payId;
    @NotNull
    @Min(value = 500, message = "Minimum kiritilishi kerak bo'lgan qiymat 500 sum")
    private Double amount;
    @NotBlank
    private String payerFullName;
}

