package uz.pdp.comunicationsystem.payload.request.filial;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FilialDTO {
    @NotBlank
    private String name;
    private DirectorDTO director;
}
