package uz.pdp.comunicationsystem.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SimCardDTO {
    @NotBlank
    @Size(min = 2, max = 2)
    private String code;
    @NotBlank
    @Size(min = 7, max = 7)
    private String number;
    @NotBlank
    private String name;
}
