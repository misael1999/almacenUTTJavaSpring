package com.flamel.almacenutt.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "salidas_productos")
public class ValeProducto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salida_producto")
    private Long idSalidaProducto;

    @Column(name = "cantidad_solicitada")
    private Integer cantidadSolicitada;

    @Column(name ="cantidad_entregada")
    private Integer cantidadEntregada;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="id_factura_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private FacturaProducto facturaProducto;

    public Long getIdSalidaProducto() {
        return idSalidaProducto;
    }

    public void setIdSalidaProducto(Long idSalidaProducto) {
        this.idSalidaProducto = idSalidaProducto;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public Integer getCantidadEntregada() {
        return cantidadEntregada;
    }

    public void setCantidadEntregada(Integer cantidadEntregada) {
        this.cantidadEntregada = cantidadEntregada;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public FacturaProducto getFacturaProducto() {
        return facturaProducto;
    }

    public void setFacturaProducto(FacturaProducto facturaProducto) {
        this.facturaProducto = facturaProducto;
    }
}
