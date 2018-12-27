package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Factura;
import com.flamel.almacenutt.models.entity.ValeSalida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacturaService {
    void saveFactura(Factura factura);
    List<Factura> findAllFacturas();
    Factura findFacturaById(Long idFactura);
    Page<Factura> listFacturasActivas(Pageable pageable);
    Page<Factura> listFacturasEntregadas(Pageable pageable);
    Factura getFacturaByFolio(String folio);
    List<Factura> findFacturaLikeTermino(@Param("termino") String termino);
}
