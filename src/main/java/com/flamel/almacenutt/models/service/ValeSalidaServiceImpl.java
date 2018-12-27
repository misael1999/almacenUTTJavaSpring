package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.FacturaProductoDao;
import com.flamel.almacenutt.models.dao.ValeSalidaDao;
import com.flamel.almacenutt.models.entity.Factura;
import com.flamel.almacenutt.models.entity.FacturaProducto;
import com.flamel.almacenutt.models.entity.ValeSalida;
import com.flamel.almacenutt.models.model.ValesByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Service
public class ValeSalidaServiceImpl implements ValeSalidaService {

    @Autowired
    ValeSalidaDao valeSalidaDao;

    @Autowired
    FacturaProductoDao facturaProductoDao;

    @Override
    public void saveValeSalida(ValeSalida valeSalida) {
        valeSalidaDao.save(valeSalida);
    }

    @Override
    public Page<ValeSalida> listValeSalidaActivas(Pageable pageable) {
        return valeSalidaDao.listValeSalidaActivas(pageable);
    }

    @Override
    public ValeSalida findValeSalidaByNumeroRequisicion(Long numeroRequisicion) {
        return valeSalidaDao.findValeSalidaByNumeroRequisicion(numeroRequisicion);
    }

    @Override
    public List<ValeSalida> findValeSalidaByIdArea(Long idArea) {
        return valeSalidaDao.findValeSalidaByIdArea(idArea);
    }

    @Override
    public ValeSalida getValeSalidaByNumeroRequisicion(Long numeroRequisicion) {
        return valeSalidaDao.getValeSalidaByNumeroRequisicion(numeroRequisicion);
    }

    @Override
    public List<ValeSalida> getValesByTerminoLike(String termino) {
        return valeSalidaDao.getValesByTerminoLike(termino);
    }

    @Override
    public ValeSalida getValeSalidaById(Long idVale) {
        return valeSalidaDao.findById(idVale).orElse(null);
    }

    @Override
    public void updateFacturaProducto(FacturaProducto facturaProducto) {
        facturaProductoDao.save(facturaProducto);
    }

    @Override
    public ValeSalida findValeSalidaByNumeroRequisicionAndArea_IdArea(Long numeroRequisicion, Long idArea) {
        return valeSalidaDao.findValeSalidaByNumeroRequisicionAndArea_IdArea(numeroRequisicion, idArea);
    }

    @Override
    public Page<ValeSalida> listValesWithDocuments(Pageable pageable) {
        return valeSalidaDao.listValesWithDocuments(pageable);
    }


}
