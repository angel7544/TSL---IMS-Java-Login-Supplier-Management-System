
package ui;

import models.Product;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utility class for generating PDF and Excel reports
 */
public class ReportGenerator {
    
    /**
     * Generates a PDF report of inventory data
     */
    public static void generatePdfReport(List<Product> products, String filePath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Inventory Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        // Add date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
        
        Paragraph date = new Paragraph("Generated on: " + dateFormat.format(new Date()), normalFont);
        date.setAlignment(Element.ALIGN_CENTER);
        document.add(date);
        
        document.add(Chunk.NEWLINE);
        
        // Create table
        PdfPTable table = new PdfPTable(6); // 6 columns
        table.setWidthPercentage(100);
        
        // Add headers
        String[] headers = {"ID", "Name", "Description", "Price", "Quantity", "Total Value"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Add data
        double totalValue = 0;
        int totalQuantity = 0;
        
        for (Product product : products) {
            double productValue = product.getPrice() * product.getQuantity();
            totalValue += productValue;
            totalQuantity += product.getQuantity();
            
            table.addCell(String.valueOf(product.getId()));
            table.addCell(product.getName());
            table.addCell(product.getDescription());
            table.addCell(String.format("$%.2f", product.getPrice()));
            table.addCell(String.valueOf(product.getQuantity()));
            table.addCell(String.format("$%.2f", productValue));
        }
        
        document.add(table);
        
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Total Products: " + products.size(), normalFont));
        
        // Initialize variables for summary
        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0;
        
        for (Product product : products) {
            minPrice = Math.min(minPrice, product.getPrice());
            maxPrice = Math.max(maxPrice, product.getPrice());
        }
        
        document.add(new Paragraph("Total Quantity: " + totalQuantity, normalFont));
        document.add(new Paragraph(String.format("Total Inventory Value: $%.2f", totalValue), normalFont));
        
        document.close();
    }
    
    public static void generateExcelReport(List<Product> products, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory Report");
        
        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Description", "Price", "Quantity", "Value", "Sold"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Add data rows
        int rowNum = 1;
        double totalValue = 0;
        int totalQuantity = 0;
        int totalSold = 0;
        
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            double value = product.getPrice() * product.getQuantity();
            totalValue += value;
            totalQuantity += product.getQuantity();
            totalSold += product.getSold();
            
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getDescription());
            row.createCell(3).setCellValue(product.getPrice());
            row.createCell(4).setCellValue(product.getQuantity());
            row.createCell(5).setCellValue(value);
            row.createCell(6).setCellValue(product.getSold());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Create summary sheet
        Sheet summarySheet = workbook.createSheet("Summary");
        
        // Define summary headers and values
        String[] summaryLabels = {
            "Total Products",
            "Total Quantity",
            "Total Inventory Value",
            "Total Items Sold",
            "Average Price",
            "Minimum Price",
            "Maximum Price"
        };
        
        Object[] summaryValues = {
            products.size(),
            totalQuantity,
            totalValue,
            totalSold,
            products.isEmpty() ? 0 : totalValue / totalQuantity,
            products.isEmpty() ? 0 : products.stream().mapToDouble(Product::getPrice).min().orElse(0),
            products.isEmpty() ? 0 : products.stream().mapToDouble(Product::getPrice).max().orElse(0)
        };
        
        // Create summary header
        Row summaryHeaderRow = summarySheet.createRow(0);
        Cell headerCell = summaryHeaderRow.createCell(0);
        headerCell.setCellValue("Summary Information");
        headerCell.setCellStyle(headerStyle);
        
        // Create summary data
        CellStyle summaryStyle = workbook.createCellStyle();
        summaryStyle.setAlignment(HorizontalAlignment.LEFT);
        
        for (int i = 0; i < summaryLabels.length; i++) {
            Row row = summarySheet.createRow(i + 2);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(summaryLabels[i]);
            
            Cell valueCell = row.createCell(1);
            if (summaryValues[i] instanceof Number) {
                if (summaryValues[i] instanceof Double) {
                    valueCell.setCellValue((Double) summaryValues[i]);
                } else if (summaryValues[i] instanceof Integer) {
                    valueCell.setCellValue((Integer) summaryValues[i]);
                }
            }
        }
        
        // Auto-size columns for summary
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);
        
        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        
        workbook.close();
    }
}
