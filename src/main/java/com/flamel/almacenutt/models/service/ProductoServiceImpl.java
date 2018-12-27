package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ProductoDao;
import com.flamel.almacenutt.models.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoDao productoDao;

    @Override
    public void saveProducto(Producto producto) {
        productoDao.save(producto);
    }

    @Override
    public Page<Producto> findAllProductos(Pageable pageable) {
        return productoDao.findAllProductos(pageable);
    }


    @Override
    public Producto getProductoByClaveAndDescripcion(String clave, String descripcion) {
        return productoDao.getProductoByClaveAndDescripcion(clave, descripcion);
    }

    @Override
    public List<Producto> findAllProductosByDescripcionLike(String descripcion) {
        return productoDao.findAllProductosByDescripcionLike(descripcion);
    }



}
