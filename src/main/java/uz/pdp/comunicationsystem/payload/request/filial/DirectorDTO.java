package uz.pdp.comunicationsystem.payload.request.filial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DirectorDTO {
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @NotNull
    @Size(min = 7)
    private String password;
    @NotNull
    @Size(min = 4)
    private String username;
}
