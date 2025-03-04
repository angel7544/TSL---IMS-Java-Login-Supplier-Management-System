package ui;

import models.Product;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

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
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Paragraph date = new Paragraph("Generated on: " + dateFormat.format(new Date()), normalFont);
        date.setAlignment(Element.ALIGN_CENTER);
        document.add(date);

        document.add(Chunk.NEWLINE);

        // Create table
        PdfPTable table = new PdfPTable(6); // 6 columns
        table.setWidthPercentage(100);

        // Set table headers
        String[] headers = {"ID", "Name", "Description", "Price", "Quantity", "Sold"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Add data
        for (Product product : products) {
            table.addCell(String.valueOf(product.getId()));
            table.addCell(product.getName());
            table.addCell(product.getDescription());
            table.addCell(String.format("$%.2f", product.getPrice()));
            table.addCell(String.valueOf(product.getQuantity()));
            table.addCell(String.valueOf(product.getSold()));
        }

        document.add(table);

        // Add summary
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Total Products: " + products.size(), normalFont));

        // Calculate total inventory value
        double totalValue = 0;
        int totalQuantity = 0;
        for (Product product : products) {
            totalValue += product.getPrice() * product.getQuantity();
            totalQuantity += product.getQuantity();
        }

        document.add(new Paragraph("Total Quantity: " + totalQuantity, normalFont));
        document.add(new Paragraph(String.format("Total Inventory Value: $%.2f", totalValue), normalFont));

        document.close();
    }

    public static void generateExcelReport(List<Product> products, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory Report");

        // Create header styles
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Create headers
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Name", "Description", "Price", "Quantity", "Sold", "Total Value"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Add data
        int rowNum = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getDescription());
            row.createCell(3).setCellValue(product.getPrice());
            row.createCell(4).setCellValue(product.getQuantity());
            row.createCell(5).setCellValue(product.getSold());
            row.createCell(6).setCellValue(product.getPrice() * product.getQuantity());
        }

        // Auto size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Add summary sheet
        Sheet summarySheet = workbook.createSheet("Summary");

        // Calculate summary data
        double totalValue = 0;
        int totalQuantity = 0;
        int totalSold = 0;

        for (Product product : products) {
            totalValue += product.getPrice() * product.getQuantity();
            totalQuantity += product.getQuantity();
            totalSold += product.getSold();
        }

        // Create summary headers
        Row summaryHeaderRow = summarySheet.createRow(0);
        Cell headerCell = summaryHeaderRow.createCell(0);
        headerCell.setCellValue("Summary");
        headerCell.setCellStyle(headerStyle);

        // Create summary content
        String[] summaryLabels = {"Total Products", "Total Quantity", "Total Sold", "Total Value"};
        Object[] summaryValues = {products.size(), totalQuantity, totalSold, totalValue};

        for (int i = 0; i < summaryLabels.length; i++) {
            Row row = summarySheet.createRow(i + 2);
            row.createCell(0).setCellValue(summaryLabels[i]);

            if (i == 3) { // Total value - format as currency
                row.createCell(1).setCellValue(totalValue);
            } else {
                row.createCell(1).setCellValue(Double.parseDouble(summaryValues[i].toString()));
            }
        }

        // Auto size columns
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);

        // Write to file
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }
}