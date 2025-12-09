package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Notificacion.PushSubscriptionDTO;
import com.nautica.backend.nautica_ies_backend.models.PushSubscription;
import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.repository.PushSubscriptionRepository;

@RestController
@RequestMapping("/api/notificaciones/suscripciones")
public class NotificacionPushController {

    private final PushSubscriptionRepository repository;

    public NotificacionPushController(PushSubscriptionRepository repository) {
        this.repository = repository;
    }

   @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registrar(
            @RequestBody PushSubscriptionDTO dto,
            @AuthenticationPrincipal Usuario usuarioActual
    ) {
        if (dto.getEndpoint() == null || dto.getKeys() == null || usuarioActual == null) {
            return;
        }

        PushSubscription sub = repository
            .findByEndpoint(dto.getEndpoint())
            .orElseGet(PushSubscription::new);

        sub.setEndpoint(dto.getEndpoint());
        sub.setP256dh(dto.getKeys().getP256dh());
        sub.setAuth(dto.getKeys().getAuth());
        sub.setUsuario(usuarioActual);

        repository.save(sub);
    }
}
