package uz.pdp.comunicationsystem.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.pdp.comunicationsystem.entity.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;

@Component
public class JWTProvider {
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    @Value("${spring.jwt.expiration}")
    private Long expiration;

    public String generateToken(String username, Set<Role> roles) {
        return Jwts
                .builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration * 1000))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("No to'g'ri yaratilgan to'ken");
        } catch (UnsupportedJwtException e) {
            System.out.println("To'ken qo'llab quvvatlanmaydi");
        } catch (ExpiredJwtException e) {
            System.out.println("Muddati o'tgan to'ken");
        } catch (IllegalArgumentException e) {
            System.out.println("Bo'sh to'ken");
        } catch (SignatureException e) {
            System.out.println("Haqiqiy bo'lmagan to'ken");
        }
        return false;
    }

    public Claims getClaimsObjectFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
