package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoService {

     void saveProducto(Producto producto);
     Page<Producto> findAllProductos(Pageable pageable);
     Producto getProductoByClave(String clave);
     List<Producto> findAllProductosByDescripcionLike(String descripcion);

}
