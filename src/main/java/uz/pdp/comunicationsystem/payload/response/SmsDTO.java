package uz.pdp.comunicationsystem.payload.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsDTO {
    @NotBlank
    private String code;
    @NotBlank
    private String number;
}
