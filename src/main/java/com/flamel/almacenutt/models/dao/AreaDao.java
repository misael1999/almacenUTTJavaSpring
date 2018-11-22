package com.flamel.almacenutt.models.dao;

import com.flamel.almacenutt.models.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaDao extends JpaRepository<Area, Long> {

    Area findAreaByNombre(String nombre);
}
