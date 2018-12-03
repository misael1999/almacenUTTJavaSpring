package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoDao extends JpaRepository<Producto,Long> {

    Producto getProductoByDescripcion(String descripcion);
    @Query("select p from Producto p order by p.idProducto desc")
    Page<Producto> findAllProductos(Pageable pageable);

    @Query("select p from Producto p where p.descripcion LIKE CONCAT('%',:descripcion,'%')")
    List<Producto> findAllProductosByDescripcionLike(@Param("descripcion") String descripcion);
}
