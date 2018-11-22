package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaDao extends JpaRepository<Factura,Long> {
    @Query("select f from Factura f join fetch f.items i join fetch i.producto where f.status=1")
    List<Factura> findAllFacturasActivas();
    @Query("select f from Factura f join fetch f.items i join fetch i.producto where f.status=0")
    List<Factura> findAllFacturasEntregadas();

//    @Query("select f from Factura f join fetch f.items i join fetch i.producto where f.folio = ?1")
    Factura getFacturaByFolio(String folio);
}
