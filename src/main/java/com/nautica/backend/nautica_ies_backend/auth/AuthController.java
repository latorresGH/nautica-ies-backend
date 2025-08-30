package com.nautica.backend.nautica_ies_backend.auth;

import com.nautica.backend.nautica_ies_backend.auth.dto.*;
import com.nautica.backend.nautica_ies_backend.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authManager,
                          UserDetailsService uds,
                          JwtService jwtService) {
        this.authManager = authManager;
        this.userDetailsService = uds;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authManager.authenticate(authToken); // lanza excepción si falla

        var user = userDetailsService.loadUserByUsername(request.username());
        var access = jwtService.generateAccessToken(user, java.util.Map.of(
                "rol", user.getAuthorities().stream().findFirst().map(Object::toString).orElse("USER")
        ));
        var refresh = jwtService.generateRefreshToken(user);
        return new JwtResponse(access, refresh);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody RefreshTokenRequest request) {
        var username = jwtService.extractUsername(request.refreshToken());
        var user = userDetailsService.loadUserByUsername(username);

        // validá refresh al menos con expiración/subject
        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new BadCredentialsException("Refresh token inválido");
        }
        var access = jwtService.generateAccessToken(user, java.util.Map.of());
        return new JwtResponse(access, request.refreshToken());
    }
}
