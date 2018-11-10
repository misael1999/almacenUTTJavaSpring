package com.flamel.almacenutt.models.service;

import com.flamel.almacenutt.models.dao.ProductoDao;
import com.flamel.almacenutt.models.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Producto> listAllProducts() {
        return productoDao.findAll();
    }

    @Override
    public Producto getProductoByDescripcion(String descripcion) {
        return productoDao.getProductoByDescripcion(descripcion);
    }

    @Override
    public List<Producto> getProductosByDescripcionLike(String descripcion) {
        return productoDao.getProductosByDescripcionLike(descripcion);
    }



}
