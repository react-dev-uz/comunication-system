package uz.pdp.comunicationsystem.payload.action;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class Actions {
    public static String getToken(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer"))
            return header.replace("Bearer ", "");
        return null;
    }
}
