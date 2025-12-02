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

    public void enviarAnuncio(Anuncio anuncio) {
        List<PushSubscription> subs = repository.findAll();
        if (subs.isEmpty()) return;

        String payload = """
                {
                  "title": "%s",
                  "body": "%s",
                  "url": "/cliente"
                }
                """
                .formatted(escapeJson(anuncio.getTitulo()), escapeJson(anuncio.getMensaje()));

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
}
