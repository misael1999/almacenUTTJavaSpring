package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.model.ReporteProducto;
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
    @Query("select p from Producto p where p.status = 1 order by p.idProducto desc")
    Page<Producto> findAllProductos(Pageable pageable);

    @Query("select p from Producto p where p.descripcion LIKE CONCAT('%',:descripcion,'%') or p.clave LIKE CONCAT('%',:descripcion,'%')")
    List<Producto> findAllProductosByDescripcionLike(@Param("descripcion") String descripcion);

    // REPORTES
    @Query(nativeQuery = true, value = "select p.*, pr.nombre as proveedor from productos p inner join proveedores pr on pr.id_proveedor = p.id_proveedor " +
            "where month(p.fecha_creacion) = ?1 and year(p.fecha_creacion) = ?2 order by p.fecha_creacion desc ")
    List<ReporteProducto> findAllProductosByDate(Integer mes, Integer ano);

}
