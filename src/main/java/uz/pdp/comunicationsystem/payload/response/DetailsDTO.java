package uz.pdp.comunicationsystem.payload.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DetailsDTO {
    @NotNull
    @Min(0)
    private Double amount;
    @NotNull
    private UUID simCardId;
    @NotNull
    private String action;
}
