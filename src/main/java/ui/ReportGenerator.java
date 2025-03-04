
package ui;

import models.Product;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ReportGenerator {
    
    public static void generateReport(List<Product> products, String filePath, String type) throws Exception {
        if (type.equalsIgnoreCase("pdf")) {
            generatePdfReport(products, filePath);
        } else if (type.equalsIgnoreCase("excel")) {
            generateExcelReport(products, filePath);
        } else {
            throw new IllegalArgumentException("Unsupported report type: " + type);
        }
    }
    
    private static void generatePdfReport(List<Product> products, String filePath) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Inventory Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        // Add date
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
        Paragraph date = new Paragraph("Generated on: " + new Date(), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
        
        document.add(Chunk.NEWLINE);
        
        // Create table
        PdfPTable table = new PdfPTable(6); // 6 columns
        table.setWidthPercentage(100);
        
        // Set table header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell headerCell;
        
        String[] headers = {"ID", "Name", "Description", "Price", "Quantity", "Sold"};
        for (String header : headers) {
            headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(headerCell);
        }
        
        // Add data rows
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
        for (Product product : products) {
            table.addCell(new Phrase(String.valueOf(product.getId()), dataFont));
            table.addCell(new Phrase(product.getName(), dataFont));
            table.addCell(new Phrase(product.getDescription(), dataFont));
            table.addCell(new Phrase(String.format("$%.2f", product.getPrice()), dataFont));
            table.addCell(new Phrase(String.valueOf(product.getQuantity()), dataFont));
            table.addCell(new Phrase(String.valueOf(product.getSold()), dataFont));
        }
        
        document.add(table);
        
        // Add summary
        document.add(Chunk.NEWLINE);
        
        int totalItems = 0;
        int totalSold = 0;
        double totalValue = 0.0;
        
        for (Product product : products) {
            totalItems += product.getQuantity();
            totalSold += product.getSold();
            totalValue += product.getPrice() * product.getQuantity();
        }
        
        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Paragraph summary = new Paragraph();
        summary.add(new Chunk("Summary:\n", summaryFont));
        summary.add(new Chunk("Total Products: " + products.size() + "\n"));
        summary.add(new Chunk("Total Items in Stock: " + totalItems + "\n"));
        summary.add(new Chunk("Total Items Sold: " + totalSold + "\n"));
        summary.add(new Chunk("Total Inventory Value: $" + String.format("%.2f", totalValue) + "\n"));
        
        document.add(summary);
        
        document.close();
    }
    
    private static void generateExcelReport(List<Product> products, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory Report");
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Description", "Price", "Quantity", "Sold", "Stock Value"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        CellStyle currencyStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        currencyStyle.setDataFormat(format.getFormat("$#,##0.00"));
        
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getDescription());
            
            Cell priceCell = row.createCell(3);
            priceCell.setCellValue(product.getPrice());
            priceCell.setCellStyle(currencyStyle);
            
            row.createCell(4).setCellValue(product.getQuantity());
            row.createCell(5).setCellValue(product.getSold());
            
            Cell valueCell = row.createCell(6);
            valueCell.setCellValue(product.getPrice() * product.getQuantity());
            valueCell.setCellStyle(currencyStyle);
        }
        
        // Create summary
        rowNum += 2;
        Row summaryLabelRow = sheet.createRow(rowNum++);
        summaryLabelRow.createCell(0).setCellValue("Summary");
        
        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        summaryLabelRow.getCell(0).setCellStyle(boldStyle);
        
        int totalItems = 0;
        int totalSold = 0;
        double totalValue = 0.0;
        
        for (Product product : products) {
            totalItems += product.getQuantity();
            totalSold += product.getSold();
            totalValue += product.getPrice() * product.getQuantity();
        }
        
        Row totalProductsRow = sheet.createRow(rowNum++);
        totalProductsRow.createCell(0).setCellValue("Total Products:");
        totalProductsRow.createCell(1).setCellValue(products.size());
        
        Row totalItemsRow = sheet.createRow(rowNum++);
        totalItemsRow.createCell(0).setCellValue("Total Items in Stock:");
        totalItemsRow.createCell(1).setCellValue(totalItems);
        
        Row totalSoldRow = sheet.createRow(rowNum++);
        totalSoldRow.createCell(0).setCellValue("Total Items Sold:");
        totalSoldRow.createCell(1).setCellValue(totalSold);
        
        Row totalValueRow = sheet.createRow(rowNum++);
        totalValueRow.createCell(0).setCellValue("Total Inventory Value:");
        Cell totalValueCell = totalValueRow.createCell(1);
        totalValueCell.setCellValue(totalValue);
        totalValueCell.setCellStyle(currencyStyle);
        
        // Auto size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        
        workbook.close();
    }
}
