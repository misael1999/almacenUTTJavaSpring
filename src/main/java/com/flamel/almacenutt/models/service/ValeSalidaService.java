package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.ValeSalida;
import com.flamel.almacenutt.models.model.ValesByArea;

import java.util.List;

public interface ValeSalidaService {

    // VALES DE SALIDAS

    void saveValeSalida(ValeSalida valeSalida);
    List<ValeSalida> listValeSalidaActivas();
    List<ValeSalida> listValeSalidaEntregadas();
    ValeSalida findValeSalidaByNumeroRequisicion(Long numeroRequisicion);


}
