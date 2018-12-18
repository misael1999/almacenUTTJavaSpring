package com.flamel.almacenutt.models.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "areas")
public class Area implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area")
    private Long idArea;
    @Column(unique = true)
    private String nombre;
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String responsable;

    private Boolean status;

    public Long getIdArea() {
        return idArea;
    }

    public void setIdArea(Long idArea) {
        this.idArea = idArea;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.toLowerCase();
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable.toLowerCase();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    private static final long serialVersionUID = 1L;

}
