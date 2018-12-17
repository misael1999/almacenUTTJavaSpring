package com.flamel.almacenutt.controllers;

import com.flamel.almacenutt.models.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ResponseEntity<?> getInfoDashboard() {

        Map<String, Object> response = new HashMap<>();
        response.put("totalProductos", dashboardService.getTotalProductos());
        response.put("totalFacturas", dashboardService.getTotalFacturas());
        response.put("totalVales", dashboardService.getTotalValeSalida());
        response.put("totalProveedores", dashboardService.getTotalProveedores());
        Sort sort = Sort.by("idProducto").descending();
        response.put("productosRecientes", dashboardService.productoRecientes(PageRequest.of(0, 8, sort)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
