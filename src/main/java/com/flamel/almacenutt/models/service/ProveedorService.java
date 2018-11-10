package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProveedorService {

    List<Proveedor> listAllProveedores();
    // Guardar y actualizar
    void saveProveedor(Proveedor proveedor);
    // Buscar proveedor por nombre
    Proveedor getProveedorByNombre(String nombre);

}
