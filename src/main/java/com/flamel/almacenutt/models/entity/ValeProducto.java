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
    private Integer cantidaSolicitada;

    @Column(name ="cantidad_entregada")
    private Integer cantidadEntregada;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="id_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;

    public Long getIdSalidaProducto() {
        return idSalidaProducto;
    }

    public void setIdSalidaProducto(Long idSalidaProducto) {
        this.idSalidaProducto = idSalidaProducto;
    }

    public Integer getCantidaSolicitada() {
        return cantidaSolicitada;
    }

    public void setCantidaSolicitada(Integer cantidaSolicitada) {
        this.cantidaSolicitada = cantidaSolicitada;
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }


}
