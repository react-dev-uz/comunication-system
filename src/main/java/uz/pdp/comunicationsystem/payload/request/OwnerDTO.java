package uz.pdp.comunicationsystem.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OwnerDTO {
    @NotNull
    @Size(min = 4, max = 64)
    private String username;
    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @NotNull
    @Size(min = 7)
    private String password;
    private Integer roleId;
    private Long filialId;
}
