package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.ValeSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ValeSalidaDao extends JpaRepository<ValeSalida, Long> {

    @Query("select v from ValeSalida v join fetch v.items i join fetch i.producto where v.status = 1")
    List<ValeSalida> listValeSalidaActivas();

    @Query("select v from ValeSalida v join fetch v.items i join fetch i.producto where v.status = 0")
    List<ValeSalida> listValeSalidaEntregadas();

//    ValeSalida getValeSalidaById(Long idValeSalida);



}
