package com.flamel.almacenutt.models.service;

import net.sf.jasperreports.engine.JRDataSource;

import java.util.Map;

public interface JasperReportService {

    byte[] generatePDFReport(String inputFileName, Map<String, Object> params, JRDataSource dataSource);

}
