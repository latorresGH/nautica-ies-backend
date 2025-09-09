package com.nautica.backend.nautica_ies_backend.auth;

import com.nautica.backend.nautica_ies_backend.auth.dto.*;
import com.nautica.backend.nautica_ies_backend.security.JwtService;
import com.nautica.backend.nautica_ies_backend.services.UsuarioService;

import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authManager,
                          UserDetailsService uds,
                          JwtService jwtService,
                          UsuarioService usuarioService) {
        this.authManager = authManager;
        this.userDetailsService = uds;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.correo(),
                request.contrasena()
        );
        authManager.authenticate(authToken); // lanza excepción si falla

        var user = userDetailsService.loadUserByUsername(request.correo());
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

        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new BadCredentialsException("Refresh token inválido");
        }
        var access = jwtService.generateAccessToken(user, java.util.Map.of());
        return new JwtResponse(access, request.refreshToken());
    }

    // @GetMapping("/me")
    // public UserSummary me(org.springframework.security.core.Authentication auth) {
    //     // auth.getName() = "username" del token → en tu caso, el correo
    //     var correo = auth.getName();
    //     var u = usuarioService.buscarPorCorreo(correo);
    //     return new UserSummary(u.getNombre(), u.getApellido(), u.getCorreo(), u.getRol().name());
    // }



    /**
     * ACTUALIZAR FUNCION EN UN FUTURO, AHORA ESTA PUESTA PARA QUE LA PUEDA LEER CUALQUIER SERVIDOR SIN PERMISOS
     * @param authHeader
     * @param correoFallback
     * @return
     */
    @GetMapping("/me")
public UserSummary me(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestParam(value = "correo", required = false) String correoFallback
) {
    String correo = null;

    // 1) Si viene Authorization: Bearer <token>, lo usamos
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        var user = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(token, user)) {
            throw new BadCredentialsException("Token inválido");
        }
        correo = username; // en tu app, el username es el correo
    }

    // 2) Fallback DEV: si no hay token, permite ?correo=...
    if (correo == null && correoFallback != null && !correoFallback.isBlank()) {
        correo = correoFallback;
    }

    if (correo == null) {
        // Importante: 401 en vez de 500
        throw new BadCredentialsException("No autenticado");
    }

    var u = usuarioService.buscarPorCorreo(correo);
    return new UserSummary(u.getNombre(), u.getApellido(), u.getCorreo(), u.getRol().name());
}
}
