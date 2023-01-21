package uz.pdp.comunicationsystem.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class TariffDTO {
    @NotEmpty
    private String name;
    @NotNull
    @Size(min = 0)
    private Double price;
    @NotNull
    @Size
    private Double transferPrice;
    @NotNull
    @Size
    private Integer mb;
    @NotNull
    @Size
    private Integer sms;
    @NotNull
    @Size
    private Integer min;
    private Double mbCost;
    private Double smsCost;
    private Double minCost;
    private boolean isCompany = false;
    private Date expireDate;    /*   bu yerda sana frontenddan long typeda data keladi keladi(1893439439031)   */
    /*
    *   Calendar instance = Calendar.getInstance();
        instance.set(2030, 0, 1);
        long timeInMillis = instance.getTimeInMillis();
        System.out.println(timeInMillis); // 1893439439031
    * */
}

/*

{
    "id": 1,
    "name": "Oddiy 15",
    "price": 15000.0,
    "transferPrice": 0.0,
    "mb": 1500,
    "sms": 500,
    "min": 1500,
    "mbCost": 40.0,
    "smsCost": 40.0,
    "minCost": 40.0
}

* */