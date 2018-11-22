package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Producto;

import java.util.List;

public interface ProductoService {

     void saveProducto(Producto producto);
     List<Producto> findAllProductos();
     Producto getProductoByDescripcion(String descripcion);
     List<Producto> findAllProductosByDescripcionLike(String descripcion);

}
