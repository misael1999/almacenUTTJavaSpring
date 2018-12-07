package com.flamel.almacenutt.util;

import com.flamel.almacenutt.models.model.ValesByArea;

import java.util.List;

public class ValeWithMes {

    private List<ValesByArea> valesPorMes;
    private String mes;

    public ValeWithMes(List<ValesByArea> valesPorMes, String mes) {
        this.valesPorMes = valesPorMes;
        this.mes = mes;
    }

    public List<ValesByArea> getValesPorMes() {
        return valesPorMes;
    }

    public void setValesPorMes(List<ValesByArea> valesPorMes) {
        this.valesPorMes = valesPorMes;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }
}
