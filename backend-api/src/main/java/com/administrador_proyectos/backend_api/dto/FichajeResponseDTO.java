package com.administrador_proyectos.backend_api.dto;

import com.administrador_proyectos.backend_api.modelo.entidad.FichajeModel;

import java.time.LocalDateTime;

public record FichajeResponseDTO(
        Long idFichaje,
        Long idUsuario,
        String nombreUsuario,
        LocalDateTime fechaHoraEntrada,
        LocalDateTime fechaHoraSalida,
        Long duracionSegundos) {

    public static FichajeResponseDTO fromEntity(FichajeModel fichaje) {
        return new FichajeResponseDTO(
                fichaje.getId(),
                fichaje.getUsuario().getId(),
                fichaje.getUsuario().getNombre(),
                fichaje.getFechaHoraEntrada(),
                fichaje.getFechaHoraSalida(),
                fichaje.getDuracionSegundos());
    }
}
