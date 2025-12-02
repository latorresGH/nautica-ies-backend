package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Notificacion.PushSubscriptionDTO;
import com.nautica.backend.nautica_ies_backend.models.PushSubscription;
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
    public void registrar(@RequestBody PushSubscriptionDTO dto) {
      if (dto.getEndpoint() == null || dto.getKeys() == null) {
        return;
      }

      PushSubscription sub = repository
          .findByEndpoint(dto.getEndpoint())
          .orElseGet(PushSubscription::new);

      sub.setEndpoint(dto.getEndpoint());
      sub.setP256dh(dto.getKeys().getP256dh());
      sub.setAuth(dto.getKeys().getAuth());

      repository.save(sub);
    }
}
