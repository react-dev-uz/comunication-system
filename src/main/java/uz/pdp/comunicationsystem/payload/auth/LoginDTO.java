package uz.pdp.comunicationsystem.payload.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
    @NotNull
    @Email(message = "Email not valid")
    private String username;
    @NotNull
    @Size(min = 7)
    private String password;
}
