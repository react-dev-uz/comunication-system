package uz.pdp.comunicationsystem.payload.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class NewSimCardDto {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @NotBlank
    private String number;
    @NotNull
    @Min(0)
    private Double price;
    @NotNull
    private Long tariffId;
    @NotNull
    private UUID clientId;
}
