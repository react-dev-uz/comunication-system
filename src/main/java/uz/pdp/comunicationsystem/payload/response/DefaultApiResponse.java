package uz.pdp.comunicationsystem.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultApiResponse<T> {
    private boolean success = true;
    private String message = "";
    private Date timestamp = new Date();
    private T data;

    public DefaultApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public DefaultApiResponse(T data) {
        this.message = message;
        this.data = data;
    }
}
