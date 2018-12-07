package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.model.ValesByArea;

import java.util.List;

public interface EstadisticaService {

    List<ValesByArea> valesByArea(Integer mes, Integer año);

}
