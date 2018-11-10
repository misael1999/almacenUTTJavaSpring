package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorDao extends JpaRepository<Proveedor,Long> {

     Proveedor getProveedorByNombre(String nombre);

}
