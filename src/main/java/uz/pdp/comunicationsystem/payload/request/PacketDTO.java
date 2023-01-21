package uz.pdp.comunicationsystem.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PacketDTO {
    @NotEmpty
    private String name;
    private String description;
    @NotNull
    @Min(0)
    private Integer amount;
    @NotNull
    @Min(0)
    private Double price;
    @NotNull
    @Min(1)
    private Integer activeDay;
    @NotNull
    private Integer typeId;
    @NotNull
    private Long ussdId;
    private List<Long> tariffsId;
}
