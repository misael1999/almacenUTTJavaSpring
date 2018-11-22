package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.FacturaDao;
import com.flamel.almacenutt.models.entity.Factura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaDao facturaDao;

    @Override
    public void saveFactura(Factura factura) {
        facturaDao.save(factura);
    }

    @Override
    public List<Factura> findAllFacturas() {
        return facturaDao.findAll();
    }

    @Override
    public Factura findFacturaById(Long idFactura) {
        return null;
    }

    @Override
    public List<Factura> listFacturasActivas() {
        return facturaDao.findAllFacturasActivas();
    }

    @Override
    public List<Factura> listFacturasEntregadas() {
        return facturaDao.findAllFacturasEntregadas();
    }

    @Override
    public Factura getFacturaByFolio(String folio) {
        return facturaDao.getFacturaByFolio(folio);
    }
}
