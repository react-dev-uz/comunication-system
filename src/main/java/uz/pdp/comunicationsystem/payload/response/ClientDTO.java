package uz.pdp.comunicationsystem.payload.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientDTO {
    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    private String jshir;   // passport serial number

    @NotBlank
    private String code;

    @NotNull
    @Size(min = 7, max = 7)
    private String number;

    @NotNull
    private Long tariffId;

    @NotNull
    private Double amountBalance;

    @NotBlank
    private String simCardName;

    private String clientType;
}
