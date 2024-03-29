package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.ValeSalida;
import com.flamel.almacenutt.models.model.ReporteGastoArea;
import com.flamel.almacenutt.models.model.ReporteProducto;
import com.flamel.almacenutt.models.model.ReporteProductosValeSalida;
import com.flamel.almacenutt.models.model.ValesByArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ValeSalidaDao extends JpaRepository<ValeSalida, Long> {

    @Query("select v from ValeSalida v  where v.status = 1")
    Page<ValeSalida> listValeSalidaActivas(Pageable pageable);

//    @Query("select v from ValeSalida v  where v.status = 0")
//    Page<ValeSalida> listValeSalidaEntregadas(Pageable pageable);

    ValeSalida findValeSalidaByNumeroRequisicion(Long numeroRequisicion);

    ValeSalida findValeSalidaByNumeroRequisicionAndArea_IdArea(Long numeroRequisicion, Long idArea);

    @Query("select v from ValeSalida v where v.area.idArea = ?1")
    List<ValeSalida> findValeSalidaByIdArea(Long idArea);

    @Query("select v from ValeSalida v where v.documento is not null")
    Page<ValeSalida> listValesWithDocuments(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT a.nombre as area, count(v.id_area) as total from areas a " +
            "left join vales_salidas v on a.id_area = v.id_area " +
            "and month(v.fecha_entrega) = ?1 and year(v.fecha_entrega) = ?2" +
            " group by a.id_area")
    List<ValesByArea> valesByArea(Integer mes, Integer ano);


    ValeSalida getValeSalidaByNumeroRequisicion(Long numeroRequisicion);

    @Query(nativeQuery = true, value = "select sp.cantidad_entregada as cantidadEntregada," +
            "sp.cantidad_solicitada as cantidadSolicitada, sp.unidad_medida as unidadMedida, p.descripcion " +
            "from vales_salidas v inner join salidas_productos sp on v.id_vale_salida = sp.id_vale_salida " +
            "inner join facturas_productos fp on fp.id_factura_producto = sp.id_factura_producto " +
            "inner join productos p on p.id_producto = fp.id_producto and v.id_vale_salida = ?1")
    List<ReporteProductosValeSalida> getProductosByValeSalidaId(Long idVale);

    @Query("select v from ValeSalida v where v.numeroRequisicion LIKE CONCAT('%',:termino,'%') or v.area.nombre LIKE CONCAT('%',:termino,'%') ")
    List<ValeSalida> getValesByTerminoLike(@Param("termino") String termino);

    @Query(nativeQuery = true, value = "select a.nombre as area, sum(sp.cantidad_entregada * p.precio) as gastos " +
            "from areas a " +
            "inner join vales_salidas v on a.id_area = v.id_area " +
            "left join salidas_productos sp on v.id_vale_salida = sp.id_vale_salida " +
            "and v.fecha_entrega between ?1 and ?2 " +
            "inner join facturas_productos fp on fp.id_factura_producto = sp.id_factura_producto " +
            "left join productos p on p.id_producto = fp.id_producto " +
            "group by a.id_area")
    List<ReporteGastoArea> getAreasGastos(String fecha1, String fecha2);

    @Query("select count(v.idValeSalida) from ValeSalida v")
    Long getTotalValeSalida();

}
