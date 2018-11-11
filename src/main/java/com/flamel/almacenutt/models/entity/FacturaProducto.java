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

    private static final long serialVersionUID = 1L;

}
