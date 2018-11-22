package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProveedorService {

    List<Proveedor> findAllProveedores();
    void saveProveedor(Proveedor proveedor);
    Proveedor getProveedorByNombre(String nombre);
    List<Proveedor> findAllProveedorLikeNombre(String nombre);

}
