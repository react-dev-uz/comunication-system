package uz.pdp.comunicationsystem.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.comunicationsystem.payload.auth.LoginDTO;
import uz.pdp.comunicationsystem.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<?> loginForStaff(@RequestBody @Valid LoginDTO loginDTO) {
        return authService.loginForOwner(loginDTO);
    }

    public ResponseEntity<?> loginForClient(@RequestBody @Valid LoginDTO loginDTO) {
        return authService.loginForClient(loginDTO);
    }
}
