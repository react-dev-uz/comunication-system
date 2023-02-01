package uz.pdp.comunicationsystem.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.comunicationsystem.component.JWTProvider;
import uz.pdp.comunicationsystem.entity.Owner;
import uz.pdp.comunicationsystem.entity.SimCard;
import uz.pdp.comunicationsystem.payload.auth.LoginDTO;
import uz.pdp.comunicationsystem.repository.OwnerRepository;
import uz.pdp.comunicationsystem.repository.SimCardRepository;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Service
public class AuthService implements UserDetailsService {
    private final OwnerRepository ownerRepository;
    private final SimCardRepository simCardRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(OwnerRepository ownerRepository,
                       SimCardRepository simCardRepository,
                       @Lazy AuthenticationManager authenticationManager,
                       JWTProvider jwtProvider,
                       @Lazy PasswordEncoder passwordEncoder) {
        this.ownerRepository = ownerRepository;
        this.simCardRepository = simCardRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> loginForOwner(LoginDTO loginDTO) {
        try {
            Owner owner = (Owner) authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            String token = jwtProvider.generateToken(owner.getUsername(), owner.getRoles());

//            Optional<Owner> optionalOwner = ownerRepository.findByUsername(loginDTO.getUsername());
//            if (optionalOwner.isEmpty()) return status(HttpServletResponse.SC_UNAUTHORIZED).body("username not found");
//            Owner owner = optionalOwner.get();
//
//            if (!owner.getPassword().equals(passwordEncoder.encode(loginDTO.getPassword())))
//                return status(HttpServletResponse.SC_UNAUTHORIZED).body("Password incorrect");
//
            return ok(token);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return status(HttpServletResponse.SC_UNAUTHORIZED).body("Username or password is invalid");
        }
    }

    public ResponseEntity<?> loginForClient(LoginDTO loginDTO) {
        try {
            SimCard simCard = (SimCard) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            String token = jwtProvider.generateToken(loginDTO.getUsername(), simCard.getClient().getRoles());
            return ok(token);
        } catch (Exception e) {
            return status(HttpServletResponse.SC_UNAUTHORIZED).body("Sim card is invalid");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Owner> optionalOwner = ownerRepository.findByUsername(username);
        if (optionalOwner.isPresent()) return optionalOwner.get();
        if (username.length() > 7) {
            Optional<SimCard> optionalSimCard = simCardRepository.findByCodeAndNumber(username.substring(0, 2), username.substring(2));
            if (optionalOwner.isPresent()) return optionalSimCard.get();
        }
        throw new UsernameNotFoundException(username + " does not exist");
    }
}
