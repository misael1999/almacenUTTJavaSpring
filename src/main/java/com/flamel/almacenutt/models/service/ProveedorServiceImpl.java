package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ProveedorDao;
import com.flamel.almacenutt.models.entity.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProveedorServiceImpl implements ProveedorService{


    @Autowired
    private ProveedorDao proveedorDao;

    @Override
    public List<Proveedor> listAllProveedores() {
        return proveedorDao.findAllProveedores();
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        proveedorDao.save(proveedor);
    }

    @Override
    public Proveedor getProveedorByNombre(String nombre) {
        return proveedorDao.getProveedorByNombreAndStutusUno(nombre);
    }

    @Override
    public List<Proveedor> findProveedorLikeNombre(String nombre) {
        return proveedorDao.findProveedorLikeNombre(nombre);
    }

//    @Override
//    public void updateStatusProveedorById(Long idProveedor) {
//        proveedorDao.updateStatusProveedorById(idProveedor);
//    }

}
