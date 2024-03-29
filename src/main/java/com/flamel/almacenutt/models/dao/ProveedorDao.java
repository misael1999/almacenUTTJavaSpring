package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorDao extends JpaRepository<Proveedor, Long> {

    @Query("select p from Proveedor p where p.status = 1 and p.nombre = ?1")
    Proveedor getProveedorByNombreAndStutusUno(String nombre);

    @Query("select p from Proveedor p where p.status = 1 order by p.idProveedor asc")
    List<Proveedor> findAllProveedores();

    @Query("select p from Proveedor p where p.status = 1 order by p.idProveedor asc")
    Page<Proveedor> findAllProveedores(Pageable pageable);

    @Query("select p from Proveedor p where p.nombre LIKE CONCAT('%',:nombre,'%')")
    List<Proveedor> findProveedorLikeNombre(@Param("nombre") String nombre);

    @Query("select p.nombre from Proveedor p where p.status = 1 and p.nombre LIKE CONCAT('%',:nombre,'%') order by p.idProveedor desc")
    List<Proveedor> findProveedorByNombreTypehead(@Param("nombre") String nombre);

    @Query("select count(p.idProveedor) from Proveedor p where p.status = 1")
    Long getTotalProveedores();

}
