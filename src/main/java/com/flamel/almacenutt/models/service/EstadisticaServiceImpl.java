package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ValeSalidaDao;
import com.flamel.almacenutt.models.model.ValesByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadisticaServiceImpl implements EstadisticaService {

    @Autowired
    ValeSalidaDao valeSalidaDao;

    @Override
    public List<ValesByArea> valesByArea(Integer mes, Integer año) {
        return valeSalidaDao.valesByArea(mes, año);
    }
}
