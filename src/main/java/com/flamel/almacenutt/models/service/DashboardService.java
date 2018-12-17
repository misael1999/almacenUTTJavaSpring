package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DashboardService {
    Long getTotalFacturas();
    Long getTotalProductos();
    Long getTotalValeSalida();
    Long getTotalProveedores();
    Page<Producto> productoRecientes(Pageable pageable);


}
