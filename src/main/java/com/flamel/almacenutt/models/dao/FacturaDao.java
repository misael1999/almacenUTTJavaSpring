package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaDao extends JpaRepository<Factura,Long> {
}
