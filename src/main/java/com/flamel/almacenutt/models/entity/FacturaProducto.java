package com.flamel.almacenutt.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "facturas_productos")
public class FacturaProducto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura_producto")
    private Long idFacturaProducto;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="id_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;
    private Integer cantidad;

    @Column(name = "cantidad_restante")
    private Integer cantidadRestante;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Long getIdFacturaProducto() {
        return idFacturaProducto;
    }

    public void setIdFacturaProducto(Long idFacturaProducto) {
        this.idFacturaProducto = idFacturaProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double calcularImporte() {
        return cantidad.doubleValue() * producto.getPrecio();
    }

    public Integer getCantidadRestante() {
        return cantidadRestante;
    }

    public void setCantidadRestante(Integer cantidadRestante) {
        this.cantidadRestante = cantidadRestante;
    }

    private static final long serialVersionUID = 1L;

}
