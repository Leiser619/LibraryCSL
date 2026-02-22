package pl.csl.library.service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.csl.library.dto.AuthResponse;
import pl.csl.library.dto.LoginRequest;
import pl.csl.library.dto.RegisterRequest;
import pl.csl.library.model.User;
import pl.csl.library.repository.UserRepository;
import pl.csl.library.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public void register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EntityExistsException("Email już zajęty.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        var principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtService.generateToken(principal.getId(), principal.getUsername());
        return new AuthResponse(token);
    }
}