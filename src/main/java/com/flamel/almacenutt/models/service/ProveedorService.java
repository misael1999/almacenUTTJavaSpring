package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProveedorService {

    List<Proveedor> findAllProveedores();
    void saveProveedor(Proveedor proveedor);
    Proveedor getProveedorByNombre(String nombre);
    List<Proveedor> findAllProveedorLikeNombre(String nombre);
    Page<Proveedor> findAllProveedores(Pageable pageable);
    List<Proveedor> findProveedorByNombreTypehead(@Param("nombre") String nombre);


}
