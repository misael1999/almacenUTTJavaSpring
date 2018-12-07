package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.ValeSalida;
import com.flamel.almacenutt.models.model.ValesByArea;
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

    ValeSalida findValeSalidaByNumeroRequisicion(Long numeroRequisicion);

    @Query(nativeQuery = true, value = "SELECT a.nombre as area, count(v.id_area) as total from areas a " +
            "left join vales_salidas v on a.id_area = v.id_area " +
            "and month(v.fecha_entrega) = ?1 and year(v.fecha_entrega) = ?2" +
            " group by a.id_area")
    List<ValesByArea> valesByArea(Integer mes, Integer año);


}
