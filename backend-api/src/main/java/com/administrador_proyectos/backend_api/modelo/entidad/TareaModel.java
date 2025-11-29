package com.administrador_proyectos.backend_api.modelo.entidad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarea")
public class TareaModel {
    public enum Prioridad { BAJA, MEDIA, ALTA }
    public enum Estado { PENDIENTE, EN_PROGRESO, BLOQUEADA, COMPLETADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false, length = 20)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", referencedColumnName = "id_proyecto", nullable = false)
    private ProyectoModel proyecto;

    @ManyToOne
    @JoinColumn(name = "id_asignado", referencedColumnName = "id_usuario")
    private UsuarioModel asignado;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public ProyectoModel getProyecto() { return proyecto; }
    public void setProyecto(ProyectoModel proyecto) { this.proyecto = proyecto; }
    public UsuarioModel getAsignado() { return asignado; }
    public void setAsignado(UsuarioModel asignado) { this.asignado = asignado; }
}