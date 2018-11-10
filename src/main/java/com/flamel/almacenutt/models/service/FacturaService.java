package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Factura;

import java.util.List;

public interface FacturaService {
    void saveFactura(Factura factura);
    List<Factura> findAllFacturas();
    Factura findFacturaById(Long idFactura);
}
