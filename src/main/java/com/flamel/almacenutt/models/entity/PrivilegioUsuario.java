package com.flamel.almacenutt.models.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "privilegios_usuarios")
public class PrivilegioUsuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_privilegio_usuario")
    private Long idPrivilegioUsuario;

    @OneToOne
    @JoinColumn(name="id_privilegio")
    private Privilegio privilegio;

    public Long getIdPrivilegioUsuario() {
        return idPrivilegioUsuario;
    }

    public void setIdPrivilegioUsuario(Long idPrivilegioUsuario) {
        this.idPrivilegioUsuario = idPrivilegioUsuario;
    }

    public Privilegio getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(Privilegio privilegio) {
        this.privilegio = privilegio;
    }
}
