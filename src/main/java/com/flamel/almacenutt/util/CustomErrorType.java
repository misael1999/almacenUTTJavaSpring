package com.flamel.almacenutt.util;

import java.util.HashMap;
import java.util.Map;

public class CustomErrorType {

    String mensaje;
    Boolean ok;
    String error;

    public CustomErrorType(String mensaje, String error) {
        this.mensaje = mensaje;
        this.ok = false;
        this.error = error;
    }

    public Map<String, Object> getResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", this.ok);
        response.put("mensaje", this.mensaje);
        response.put("error", this.error);
        return response;
    }
}
