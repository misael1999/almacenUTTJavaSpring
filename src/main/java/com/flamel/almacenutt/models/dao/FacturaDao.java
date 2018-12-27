package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaDao extends JpaRepository<Factura, Long> {

    @Query("select f from Factura f where f.completada = 0")
    Page<Factura> findAllFacturasActivas(Pageable pageable);

    @Query("select f from Factura f where f.completada = 1")
    Page<Factura> findAllFacturasEntregadas(Pageable pageable);

    @Query("select f from Factura f where f.folio LIKE CONCAT('%',:termino,'%') or f.proveedor.nombre LIKE CONCAT('%',:termino,'%') ")
    List<Factura> findFacturaLikeTermino(@Param("termino") String termino);


    Factura getFacturaByFolio(String folio);

    @Query("select count(f.idFactura) from Factura f")
    Long getTotalFacturas();

}
