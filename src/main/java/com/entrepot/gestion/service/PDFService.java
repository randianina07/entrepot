package com.entrepot.gestion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.dto.MouvementListDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PDFService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateMouvementsPDF(List<MouvementListDTO> mouvements) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Titre
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Paragraph title = new Paragraph("Rapport des Mouvements")
                .setFont(fontBold)
                .setFontSize(16)
                .setFontColor(new DeviceRgb(51, 51, 51))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(title);

            // Date de génération
            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            Paragraph date = new Paragraph("Généré le: " + java.time.LocalDateTime.now().format(DATE_FORMATTER))
                .setFont(fontNormal)
                .setFontSize(9)
                .setFontColor(new DeviceRgb(102, 102, 102))
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
            document.add(date);

            // Tableau
            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 18, 12, 12, 15, 8, 20}))
                .setWidth(UnitValue.createPercentValue(100));

            // En-têtes
            addTableHeader(table, fontBold);

            // Données
            addTableData(table, mouvements, fontNormal);

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph("Total: " + mouvements.size() + " mouvement(s)")
                .setFont(fontNormal)
                .setFontSize(8)
                .setFontColor(new DeviceRgb(153, 153, 153))
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20);
            document.add(footer);
        }
        // Récupère le PDF seulement après fermeture des ressources iText
        return outputStream.toByteArray();
    }
    
    private void addTableHeader(Table table, PdfFont font) {
        String[] headers = {"Code", "Date", "Type", "Statut", "Client", "Lignes", "Opérateur"};
        
        for (String header : headers) {
            Cell cell = new Cell()
                .add(new Paragraph(header).setFont(font).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(new DeviceRgb(70, 130, 180))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
            table.addCell(cell);
        }
    }
    
    private void addTableData(Table table, List<MouvementListDTO> mouvements, PdfFont font) {
        for (MouvementListDTO mvt : mouvements) {
            table.addCell(new Cell().add(new Paragraph(truncate(mvt.getCode(), 15)).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(mvt.getDateMouvement() != null ? mvt.getDateMouvement().format(DATE_FORMATTER) : "").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(truncate(mvt.getTypeMouvement(), 12)).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(truncate(mvt.getStatutMouvement(), 10)).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(truncate(mvt.getClient() != null ? mvt.getClient() : "-", 12)).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(mvt.getNbLignes())).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(truncate(mvt.getOperateur() != null ? mvt.getOperateur() : "-", 15)).setFont(font)));
        }
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "-";
        return str.length() > maxLength ? str.substring(0, maxLength - 1) + "." : str;
    }
}
