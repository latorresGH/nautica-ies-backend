package com.nautica.backend.nautica_ies_backend.services;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.models.Anuncio;
import com.nautica.backend.nautica_ies_backend.models.PushSubscription;
import com.nautica.backend.nautica_ies_backend.repository.PushSubscriptionRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
public class PushNotificationService {

    private final PushSubscriptionRepository repository;
    private final PushService pushService;

    public PushNotificationService(
            PushSubscriptionRepository repository,
            @Value("${vapid.public}") String publicKey,
            @Value("${vapid.private}") String privateKey,
            @Value("${vapid.subject}") String subject) throws GeneralSecurityException {

        this.repository = repository;

        Security.addProvider(new BouncyCastleProvider());

        this.pushService = new PushService();
        this.pushService.setPublicKey(publicKey);
        this.pushService.setPrivateKey(privateKey);
        this.pushService.setSubject(subject);
    }

    /* ================== ANUNCIOS (lo que ya tenías) ================== */

    public void enviarAnuncio(Anuncio anuncio) {
        List<PushSubscription> subs = repository.findAll();
        if (subs.isEmpty()) return;

        String payload = buildPayload(
                anuncio.getTitulo(),
                anuncio.getMensaje(),
                "/cliente" // URL a donde querés llevar al cliente cuando hace click
        );

        enviarAGrupo(subs, payload);
    }

    /* ================== GENÉRICO ================== */

    private String buildPayload(String title, String body, String url) {
        return """
                {
                  "title": "%s",
                  "body": "%s",
                  "url": "%s"
                }
                """
                .formatted(
                        escapeJson(title),
                        escapeJson(body),
                        escapeJson(url)
                );
    }

    private void enviarAGrupo(List<PushSubscription> subs, String payload) {
        if (subs == null || subs.isEmpty()) return;

        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

        for (PushSubscription sub : subs) {
            try {
                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payloadBytes
                );
                pushService.send(notification);
            } catch (Exception e) {
                System.err.println("Error enviando push a " + sub.getEndpoint() + ": " + e.getMessage());
            }
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", " ");
    }

    /* ================== PARA TURNOS ================== */

    // 👉 al usuario cliente
    public void enviarTurnoCliente(Long idUsuarioCliente, String title, String body, String url) {
        List<PushSubscription> subs = repository.findByUsuario_IdUsuario(idUsuarioCliente);
        String payload = buildPayload(title, body, url);
        enviarAGrupo(subs, payload);
    }

    // 👉 a lista de operarios (ids de Usuario)
    public void enviarTurnoOperarios(List<Long> idsOperarios, String title, String body, String url) {
        List<PushSubscription> subs = idsOperarios.stream()
                .flatMap(id -> repository.findByUsuario_IdUsuario(id).stream())
                .toList();

        String payload = buildPayload(title, body, url);
        enviarAGrupo(subs, payload);
    }
}
