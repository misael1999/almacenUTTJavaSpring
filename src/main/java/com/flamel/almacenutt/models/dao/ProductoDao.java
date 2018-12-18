package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Producto;
import com.flamel.almacenutt.models.entity.Proveedor;
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

    Producto getProductoByClave(String clave);
    @Query("select p from Producto p where p.status = 1 order by p.idProducto desc")
    Page<Producto> findAllProductos(Pageable pageable);

    @Query("select p from Producto p where p.descripcion LIKE CONCAT('%',:descripcion,'%') or p.clave LIKE CONCAT('%',:descripcion,'%')")
    List<Producto> findAllProductosByDescripcionLike(@Param("descripcion") String descripcion);

    // REPORTES
    @Query(nativeQuery = true, value = "select p.*, pr.nombre as proveedor from productos p inner join proveedores pr on pr.id_proveedor = p.id_proveedor " +
            "where month(p.fecha_creacion) = ?1 and year(p.fecha_creacion) = ?2 order by p.fecha_creacion desc ")
    List<ReporteProducto> findAllProductosByDate(Integer mes, Integer ano);

    @Query("select p from Producto  p where p.idProveedor = ?1")
    List<Producto> getProductosByProveedor(Long idProveedor);

    @Query(nativeQuery = true, value = "select p.clave, p.descripcion, sp.cantidad_entregada as cantidad, p.unidad_medida, p.precio, pr.nombre as proveedor " +
            "from vales_salidas v " +
            "inner join salidas_productos sp on v.id_vale_salida = sp.id_vale_salida " +
            "inner join productos p on p.id_producto = sp.id_producto " +
            "inner join proveedores pr on pr.id_proveedor = p.id_proveedor " +
            "where v.id_area = ?1 and v.fecha_entrega between ?2 and ?3 ")
    List<ReporteProducto> getProductosByArea(Long idArea, String fecha1, String fecha2);

    // DASHBOARD INFO
    @Query("select count(p.idProducto) from Producto p where p.status = 1")
    Long getTotalProductos();

    @Query("select p from Producto p where p.status = 1 and p.cantidad > 0")
    Page<Producto> productoRecientes(Pageable pageable);

}
