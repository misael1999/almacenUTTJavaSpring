package com.flamel.almacenutt.models.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class JasperReportServiceImpl implements JasperReportService {

    @Override
    public byte[] generatePDFReport(String inputFileName, Map<String, Object> params, JRDataSource dataSource) {
        byte[] bytes = null;
        try {
            Path rutaAnterior = Paths.get("reports").resolve(inputFileName + ".jasper").toAbsolutePath();
            File f = rutaAnterior.toFile();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(f);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, dataSource);
            bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return bytes;
        } catch (JRException e) {
            e.printStackTrace();
        }

        return bytes;
    }
}
