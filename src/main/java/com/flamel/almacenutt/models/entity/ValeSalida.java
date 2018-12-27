package com.flamel.almacenutt.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "vales_salidas")
public class ValeSalida implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vale_salida")
    private Long idValeSalida;

    @Column(name = "numero_requisicion")
    private Long numeroRequisicion;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area")
    private Area area;

    @Column(name = "fecha_entrega")
    private String fechaEntrega;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_vale_salida")
    private List<ValeProducto> items;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_factura")
    private Factura factura;

    private boolean status = true;

    private String documento;

    public ValeSalida() {
        this.items = new ArrayList<>();
    }

    public Long getIdValeSalida() {
        return idValeSalida;
    }

    public void setIdValeSalida(Long idValeSalida) {
        this.idValeSalida = idValeSalida;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public List<ValeProducto> getItems() {
        return items;
    }

    public void setItems(List<ValeProducto> items) {
        this.items = items;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void addItemVale(ValeProducto item) {
        this.items.add(item);
    }


    public Long getNumeroRequisicion() {
        return numeroRequisicion;
    }

    public void setNumeroRequisicion(Long numeroRequisicion) {
        this.numeroRequisicion = numeroRequisicion;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
