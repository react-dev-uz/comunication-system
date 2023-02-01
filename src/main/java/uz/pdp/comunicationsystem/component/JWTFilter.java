package uz.pdp.comunicationsystem.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.pdp.comunicationsystem.payload.action.Actions;
import uz.pdp.comunicationsystem.service.AuthService;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTProvider jwtProvider;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public JWTFilter(JWTProvider jwtProvider, AuthService authService, PasswordEncoder passwordEncoder) {
        this.jwtProvider = jwtProvider;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = Actions.getToken(request);
        if (token != null && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getClaimsObjectFromToken(token).getSubject();
            UserDetails userDetails = authService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
