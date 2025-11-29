package com.administrador_proyectos.backend_api.modelo.entidad;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comentario")
public class ComentarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long idComentario;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(name = "fecha_comentario", nullable = false)
    private LocalDateTime fechaComentario = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_tarea", referencedColumnName = "id_tarea", nullable = false)
    private TareaModel tarea;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @ManyToMany
    @JoinTable(
            name = "mencion",
            joinColumns = @JoinColumn(name = "id_comentario"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private Set<UsuarioModel> menciones = new HashSet<>();

    // Getters y Setters
    public Long getIdComentario() { return idComentario; }
    public void setIdComentario(Long idComentario) { this.idComentario = idComentario; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getFechaComentario() { return fechaComentario; }
    public void setFechaComentario(LocalDateTime fechaComentario) { this.fechaComentario = fechaComentario; }
    public TareaModel getTarea() { return tarea; }
    public void setTarea(TareaModel tarea) { this.tarea = tarea; }
    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }
    public Set<UsuarioModel> getMenciones() { return menciones; }
    public void setMenciones(Set<UsuarioModel> menciones) { this.menciones = menciones; }
}