package com.nautica.backend.nautica_ies_backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Turno;

@Service
public class TurnoNotificationService {

    private final PushNotificationService pushNotificationService;

    public TurnoNotificationService(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    public void notificarTurnoCreadoCliente(Turno turno) {
        Cliente cliente = turno.getCliente();
        if (cliente == null) {
            return;
        }

        // OJO: adaptá según tu modelo: getIdUsuario() o getUsuario().getIdUsuario()
        Long idUsuarioCliente = cliente.getIdUsuario();

        String fecha = turno.getFecha() != null ? turno.getFecha().toString() : "";
        String hora = turno.getHoraInicio() != null ? turno.getHoraInicio().toString() : "";

        String title = "Turno solicitado";
        String body = "Creaste un turno para el " + fecha + " a las " + hora + ".";
        String url = "/cliente/turnos";

        pushNotificationService.enviarTurnoCliente(idUsuarioCliente, title, body, url);
    }

    public void notificarTurnoCanceladoCliente(Turno turno) {
        Cliente cliente = turno.getCliente();
        if (cliente == null) {
            return;
        }

        Long idUsuarioCliente = cliente.getIdUsuario();

        String fecha = turno.getFecha() != null ? turno.getFecha().toString() : "";
        String hora = turno.getHoraInicio() != null ? turno.getHoraInicio().toString() : "";

        String title = "Turno cancelado";
        String body = "Se canceló tu turno del " + fecha + " a las " + hora + ".";
        String url = "/cliente/turnos";

        pushNotificationService.enviarTurnoCliente(idUsuarioCliente, title, body, url);
    }

    // 🔔 NUEVO: notificar a operarios
    public void notificarTurnoCreadoOperarios(Turno turno, List<Long> idsOperarios) {
        if (idsOperarios == null || idsOperarios.isEmpty()) {
            return;
        }

        String fecha = turno.getFecha() != null ? turno.getFecha().toString() : "";
        String hora = turno.getHoraInicio() != null ? turno.getHoraInicio().toString() : "";

        String title = "Nuevo turno de cliente";
        String body = "Hay un nuevo turno para el " + fecha + " a las " + hora + ".";
        String url = "/operario/botados"; // o "/operario/lavados", o un dashboard general

        pushNotificationService.enviarTurnoOperarios(idsOperarios, title, body, url);
    }
}
