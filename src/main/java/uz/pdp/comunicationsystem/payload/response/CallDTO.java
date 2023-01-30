package uz.pdp.comunicationsystem.payload.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CallDTO {

    @NotBlank
    private String number;
    @NotBlank
    private String code;
    @NotNull
    @Min(0)
    private double seconds;
}
