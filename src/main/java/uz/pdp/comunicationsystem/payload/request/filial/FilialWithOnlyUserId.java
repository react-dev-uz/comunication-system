package uz.pdp.comunicationsystem.payload.request.filial;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class FilialWithOnlyUserId {
    @NotBlank
    private String name;
    private UUID directorId;
}
