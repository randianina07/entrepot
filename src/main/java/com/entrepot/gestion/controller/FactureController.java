package com.entrepot.gestion.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.entrepot.gestion.dto.FactureDTO;
import com.entrepot.gestion.service.FactureService;

import com.itextpdf.io.font.constants.StandardFonts;
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

@Controller
@RequestMapping("/facture")
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    /**
     * Affiche la page de facture pour un contrat.
     */
    @GetMapping("/contrat/{contratId}")
    public String voirFacture(
            @PathVariable Long contratId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date,
            Model model) {

        try {
            LocalDate factureDate = null;
            if (date != null && !date.trim().isEmpty()) {
                factureDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            }
            FactureDTO facture = factureService.genererFacture(contratId, factureDate);
            model.addAttribute("facture", facture);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Date invalide. Veuillez entrer une date au format JJ/MM/AAAA.");
        }

        return "facture/index";
    }

    /**
     * Exporte la facture en PDF.
     */
    @GetMapping("/contrat/{contratId}/pdf")
    public ResponseEntity<byte[]> exporterPdf(
            @PathVariable Long contratId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date) {

        LocalDate factureDate = null;
        if (date != null && !date.trim().isEmpty()) {
            factureDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        }
        FactureDTO dto = factureService.genererFacture(contratId, factureDate);

        try {
            byte[] pdfBytes = genererPdfFacture(dto);

            String fileName = "facture_" + contratId + "_"
                    + dto.getDateFacture().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] genererPdfFacture(FactureDTO dto) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Titre
            Paragraph title = new Paragraph("FACTURE")
                    .setFont(fontBold)
                    .setFontSize(22)
                    .setFontColor(new DeviceRgb(0, 51, 102))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(title);

            // Sous-titre
            Paragraph subtitle = new Paragraph("Contrat N° " + dto.getContratId())
                    .setFont(fontNormal)
                    .setFontSize(12)
                    .setFontColor(new DeviceRgb(102, 102, 102))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitle);

            // Separator line
            Paragraph line = new Paragraph("——————————————————————————————————————")
                    .setFont(fontNormal)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(line);

            // Informations client
            Paragraph clientTitle = new Paragraph("INFORMATIONS CLIENT")
                    .setFont(fontBold)
                    .setFontSize(14)
                    .setFontColor(new DeviceRgb(0, 51, 102))
                    .setMarginBottom(10);
            document.add(clientTitle);

            String clientInfo = "";
            if (dto.getClientPrenom() != null || dto.getClientNom() != null) {
                clientInfo += (dto.getClientPrenom() != null ? dto.getClientPrenom() + " " : "")
                        + (dto.getClientNom() != null ? dto.getClientNom() : "") + "\n";
            }
            clientInfo += "Email : " + (dto.getClientEmail() != null ? dto.getClientEmail() : "-") + "\n";
            clientInfo += "Tél : " + (dto.getClientTelephone() != null ? dto.getClientTelephone() : "-") + "\n";
            clientInfo += "Adresse : " + (dto.getClientAdresse() != null ? dto.getClientAdresse() : "-");

            Paragraph clientPara = new Paragraph(clientInfo)
                    .setFont(fontNormal)
                    .setFontSize(10)
                    .setMarginBottom(20);
            document.add(clientPara);

            // Informations contrat
            Paragraph contratTitle = new Paragraph("INFORMATIONS CONTRAT")
                    .setFont(fontBold)
                    .setFontSize(14)
                    .setFontColor(new DeviceRgb(0, 51, 102))
                    .setMarginBottom(10);
            document.add(contratTitle);

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String contratInfo = "Type de zone : " + dto.getTypeZone() + "\n"
                    + "Type de contrat : " + dto.getTypeContrat() + "\n"
                    + "Volume : " + dto.getVolumeM3() + " m³\n"
                    + "Emplacements : " + (dto.getQuantiteEmplacement() != null ? dto.getQuantiteEmplacement() : "-") + "\n"
                    + "Date début : " + dto.getDateDebut().format(dateFmt) + "\n"
                    + "Date facture : " + dto.getDateFacture().format(dateFmt);

            Paragraph contratPara = new Paragraph(contratInfo)
                    .setFont(fontNormal)
                    .setFontSize(10)
                    .setMarginBottom(20);
            document.add(contratPara);

            // Détail facturation
            Paragraph detailTitle = new Paragraph("DÉTAIL FACTURATION")
                    .setFont(fontBold)
                    .setFontSize(14)
                    .setFontColor(new DeviceRgb(0, 51, 102))
                    .setMarginBottom(10);
            document.add(detailTitle);

            // Tableau
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 30, 30}))
                    .setWidth(UnitValue.createPercentValue(100));

            // En-têtes
            String[] headers = {"Libellé", "Détail", "Montant"};
            for (String h : headers) {
                Cell cell = new Cell()
                        .add(new Paragraph(h).setFont(fontBold).setFontColor(new DeviceRgb(255, 255, 255)))
                        .setBackgroundColor(new DeviceRgb(0, 51, 102))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(8);
                table.addCell(cell);
            }

            // Ligne: durée
            table.addCell(new Cell().add(new Paragraph("Durée de facturation").setFont(fontNormal)));
            table.addCell(new Cell().add(new Paragraph(dto.getDureeAffichage()).setFont(fontNormal)));
            table.addCell(new Cell().add(new Paragraph("").setFont(fontNormal)));

            // Ligne: mois
            if (dto.getDureeMois() > 0 && dto.getPrixM3Mois() != null) {
                String detailMois = dto.getDureeMois() + " mois × " + dto.getVolumeM3()
                        + " m³ × " + String.format("%,.2f", dto.getPrixM3Mois()) + " Ar/m³";
                table.addCell(new Cell().add(new Paragraph("Tarif mensuel").setFont(fontNormal)));
                table.addCell(new Cell().add(new Paragraph(detailMois).setFont(fontNormal)));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.2f Ar", dto.getTotalMois())).setFont(fontNormal)));
            }

            // Ligne: jours
            if (dto.getDureeJours() > 0 && dto.getPrixM3Jour() != null) {
                String detailJours = dto.getDureeJours() + " jour(s) × " + dto.getVolumeM3()
                        + " m³ × " + String.format("%,.2f", dto.getPrixM3Jour()) + " Ar/m³";
                table.addCell(new Cell().add(new Paragraph("Tarif journalier").setFont(fontNormal)));
                table.addCell(new Cell().add(new Paragraph(detailJours).setFont(fontNormal)));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.2f Ar", dto.getTotalJours())).setFont(fontNormal)));
            }

            document.add(table);

            // Total
            Paragraph totalTitle = new Paragraph("TOTAL : " + String.format("%,.2f", dto.getTotalGeneral()) + " Ar")
                    .setFont(fontBold)
                    .setFontSize(16)
                    .setFontColor(new DeviceRgb(0, 51, 102))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(20);
            document.add(totalTitle);

            // Footer
            Paragraph footer = new Paragraph("Généré le " + LocalDate.now().format(dateFmt)
                    + " | Société de Gestion d'Entrepôt")
                    .setFont(fontNormal)
                    .setFontSize(8)
                    .setFontColor(new DeviceRgb(153, 153, 153))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);
        }

        return outputStream.toByteArray();
    }
}
