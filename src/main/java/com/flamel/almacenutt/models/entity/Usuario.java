package com.flamel.almacenutt.models.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    @Column(name = "apellido_paterno")
    private String apellidoPaterno;
    @Column(name = "apellido_materno")
    private String apellidoMaterno;
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name="id_area")
//    private Area area;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario")
    private List<PrivilegioUsuario> privilegios;

    private String role;
    @Column(name = "nombre_usuario", unique = true)
    private String nombreUsuario;

    private String password;
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Boolean status = true;

    public Usuario() {
        this.privilegios = new ArrayList<>();
    }

    public void addPrivilegio(PrivilegioUsuario privilegioUsuario) {
        this.privilegios.add(privilegioUsuario);
    };

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long id_usuario) {
        this.idUsuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<PrivilegioUsuario> getPrivilegios() {
        return privilegios;
    }

    public void setPrivilegios(List<PrivilegioUsuario> privilegios) {
        this.privilegios = privilegios;
    }

    private static final long serialVersionUID = 1L;

}
