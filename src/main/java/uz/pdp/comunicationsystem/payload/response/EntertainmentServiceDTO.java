package uz.pdp.comunicationsystem.payload.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EntertainmentServiceDTO {
    @NotBlank
    private String name;
    @Min(0)
    private Double price;
    @Min(0)
    private Integer duration;
}
