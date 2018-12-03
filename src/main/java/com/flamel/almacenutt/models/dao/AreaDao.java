package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AreaDao extends JpaRepository<Area, Long> {

    Area findAreaByNombre(String nombre);
    @Query("select  a from Area a where a.status = 1")
    List<Area> listAllArea();
}
