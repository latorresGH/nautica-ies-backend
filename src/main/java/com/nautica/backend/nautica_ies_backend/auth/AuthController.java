// AuthController.java
package com.nautica.backend.nautica_ies_backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.auth.dto.JwtResponse;
import com.nautica.backend.nautica_ies_backend.auth.dto.LoginRequest;
import com.nautica.backend.nautica_ies_backend.auth.dto.RefreshTokenRequest;
import com.nautica.backend.nautica_ies_backend.auth.dto.UserSummary;
import com.nautica.backend.nautica_ies_backend.security.JwtService;
import com.nautica.backend.nautica_ies_backend.services.UsuarioService;
import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;

import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1) Verificar que el usuario exista (tu servicio lanza ResourceNotFoundException si no)
            usuarioService.buscarPorCorreo(request.correo());

            // 2) Autenticar credenciales (Spring Security + PasswordEncoder)
            var authToken = new UsernamePasswordAuthenticationToken(
                    request.correo(),
                    request.contrasena()
            );
            authManager.authenticate(authToken); // lanza BadCredentialsException si la pass no matchea

            // 3) Generar tokens
            var user = userDetailsService.loadUserByUsername(request.correo());
            var access = jwtService.generateAccessToken(user, Map.of(
                    "rol", user.getAuthorities().stream().findFirst().map(Object::toString).orElse("USER")
            ));
            var refresh = jwtService.generateRefreshToken(user);

            return ResponseEntity.ok(new JwtResponse(access, refresh));

        } catch (ResourceNotFoundException e) {
            // Usuario no existe
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "USUARIO_INEXISTENTE"));
        } catch (BadCredentialsException e) {
            // Usuario existe, pero la contraseña es incorrecta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "CONTRASENA_INCORRECTA"));
        } catch (Exception e) {
            // Cualquier cosa inesperada → 500 genérico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "ERROR_INTERNO"));
        }
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

    @GetMapping("/me")
    public UserSummary me(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "correo", required = false) String correoFallback
    ) {
        String correo = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            var user = userDetailsService.loadUserByUsername(username);
            if (!jwtService.isTokenValid(token, user)) {
                throw new BadCredentialsException("Token inválido");
            }
            correo = username;
        }

        if (correo == null && correoFallback != null && !correoFallback.isBlank()) {
            correo = correoFallback;
        }

        if (correo == null) {
            throw new BadCredentialsException("No autenticado");
        }

        var u = usuarioService.buscarPorCorreo(correo);
        return new UserSummary(u.getNombre(), u.getApellido(), u.getCorreo(), u.getRol().name());
    }
}
