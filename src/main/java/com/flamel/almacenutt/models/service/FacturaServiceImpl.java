package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.FacturaDao;
import com.flamel.almacenutt.models.dao.ValeSalidaDao;
import com.flamel.almacenutt.models.entity.Factura;
import com.flamel.almacenutt.models.entity.ValeSalida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaDao facturaDao;

    @Autowired
    private ValeSalidaDao valeSalidaDao;

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
    public Page<Factura> listFacturasActivas(Pageable pageable) {
        return facturaDao.findAllFacturasActivas(pageable);
    }

    @Override
    public Page<Factura> listFacturasEntregadas(Pageable pageable) {
        return facturaDao.findAllFacturasEntregadas(pageable);
    }

    @Override
    public Factura getFacturaByFolio(String folio) {
        return facturaDao.getFacturaByFolio(folio);
    }

    @Override
    public List<Factura> findFacturaLikeTermino(String termino) {
        return facturaDao.findFacturaLikeTermino(termino);
    }

    @Override
    public Page<Factura> listFacturasWithDocuments(Pageable pageable) {
        return facturaDao.listFacturasWithDocuments(pageable);
    }


//    @Override
//    public ValeSalida getValeSalidaById(Long idValeSalida) {
//        return valeSalidaDao.getValeSalidaById(idValeSalida);
//    }
}
