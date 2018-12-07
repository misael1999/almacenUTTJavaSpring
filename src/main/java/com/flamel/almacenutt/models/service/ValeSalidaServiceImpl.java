package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ValeSalidaDao;
import com.flamel.almacenutt.models.entity.ValeSalida;
import com.flamel.almacenutt.models.model.ValesByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValeSalidaServiceImpl implements ValeSalidaService {

    @Autowired
    ValeSalidaDao valeSalidaDao;

    @Override
    public void saveValeSalida(ValeSalida valeSalida) {
        valeSalidaDao.save(valeSalida);
    }

    @Override
    public List<ValeSalida> listValeSalidaActivas() {
        return valeSalidaDao.listValeSalidaActivas();
    }

    @Override
    public List<ValeSalida> listValeSalidaEntregadas() {
        return valeSalidaDao.listValeSalidaEntregadas();
    }

    @Override
    public ValeSalida findValeSalidaByNumeroRequisicion(Long numeroRequisicion) {
        return valeSalidaDao.findValeSalidaByNumeroRequisicion(numeroRequisicion);
    }



}
