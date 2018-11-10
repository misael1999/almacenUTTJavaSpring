package com.flamel.almacenutt.util;

import java.util.HashMap;
import java.util.Map;

public class CustomResponseType {

    private String mensaje;
    private Boolean ok;
    private String nombreElemento;
    private Object elemento;
    private String titulo;

    public CustomResponseType(String mensaje, String nombreElemento, Object elemento, String titulo) {
        this.mensaje = mensaje;
        ok = true;
        this.nombreElemento = nombreElemento;
        this.elemento = elemento;
        this.titulo = titulo;
    }

    public Map<String, Object> getResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", this.ok);
        response.put(this.nombreElemento, this.elemento);
        response.put("mensaje", this.mensaje);
        response.put("titulo", this.titulo);
        return response;
    }
}
