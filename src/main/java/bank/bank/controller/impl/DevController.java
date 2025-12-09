package bank.bank.controller.impl;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/dev")
public class DevController {

    @GetMapping("/api-doc")
    public ResponseEntity<byte[]> getApiDoc() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            // Set Landscape orientation for wider rows
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document document = new Document(pdf);

            // Create font
            PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);

            document.add(new Paragraph("Backend API Documentation").setBold().setFontSize(18));
            document.add(new Paragraph("Base URL: http://localhost:8080").setFontSize(12));
            document.add(new Paragraph("\n"));

            // Define column widths (Endpoint=30%, Description=20%, Usage=20%, JSON=30%)
            float[] colWidths = { 30, 20, 20, 30 };

            // Customer Endpoints
            document.add(new Paragraph("Customer Endpoints (/customer)").setBold().setFontSize(14));
            Table t1 = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();
            t1.addHeaderCell(new Paragraph("Endpoint").setBold());
            t1.addHeaderCell(new Paragraph("Description").setBold());
            t1.addHeaderCell(new Paragraph("Usage").setBold());
            t1.addHeaderCell(new Paragraph("Request JSON").setBold());

            addRow(t1, "POST /customer/create", "Register customer", "DtoCustomerIU -> DtoCustomer",
                    "{\n  \"fullName\": \"...\",\n  \"email\": \"...\",\n  \"emailPassword\": \"...\",\n  \"telephone\": \"+994...\",\n  \"birthDate\": \"YYYY-MM-DD\"\n}",
                    font);
            addRow(t1, "GET /customer/{id}", "Get details", "Path: id -> DtoCustomer", "-", font);
            addRow(t1, "POST /customer/login", "Login", "DtoLoginRequest -> DtoCustomer",
                    "{\n  \"email\": \"...\",\n  \"password\": \"...\"\n}", font);
            addRow(t1, "POST /customer/register/send-otp", "Send OTP", "DtoSendOTP -> String",
                    "{\n  \"email\": \"...\"\n}", font);
            addRow(t1, "POST /customer/register/verify-otp", "Verify OTP", "DtoVerifyOTP -> String",
                    "{\n  \"email\": \"...\",\n  \"code\": \"...\"\n}", font);
            document.add(t1);
            document.add(new Paragraph("\n"));

            // Card Endpoints
            document.add(new Paragraph("Card Endpoints (/card)").setBold().setFontSize(14));
            Table t2 = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();
            t2.addHeaderCell(new Paragraph("Endpoint").setBold());
            t2.addHeaderCell(new Paragraph("Description").setBold());
            t2.addHeaderCell(new Paragraph("Usage").setBold());
            t2.addHeaderCell(new Paragraph("Request JSON").setBold());

            addRow(t2, "POST /card/create/{customerId}", "Create card", "Path: id, DtoCardIU -> DtoCard",
                    "{\n  \"cardPassword\": \"...\",\n  \"cardBrand\": \"VISA\",\n  \"cardType\": \"DEBIT\",\n  \"currency\": \"AZN\"\n}",
                    font);
            addRow(t2, "POST /card/transfer", "Transfer", "DtoTransferRequest -> String",
                    "{\n  \"fromCardNumber\": \"...\",\n  \"toCardNumber\": \"...\",\n  \"amount\": 10\n}", font);
            addRow(t2, "GET /card/history/{cardId}", "History", "Path: id -> List<History>", "-", font);
            addRow(t2, "POST /card/withdraw", "Withdraw", "DtoWithdrawRequest -> String",
                    "{\n  \"fromCardNumber\": \"...\",\n  \"amount\": 10\n}", font);
            addRow(t2, "POST /card/pay", "Payment", "DtoPayRequest -> String",
                    "{\n  \"fromCard\": \"...\",\n  \"providerName\": \"...\",\n  \"amount\": 10\n}", font);
            addRow(t2, "GET /card/credit/debt", "Get Debt", "Param: card -> String", "-", font);
            addRow(t2, "POST /card/credit/pay", "Pay Debt", "Params -> String", "-", font);
            addRow(t2, "POST /card/history/{cardId}/export", "Export PDF", "Path: id -> String", "-", font);

            // PIN Reset
            addRow(t2, "POST /card/pin/reset/start", "Start Reset", "DtoPinResetStartRequest -> String",
                    "{\n  \"cardNumber\": \"...\"\n}", font);
            addRow(t2, "POST /card/pin/reset/verify", "Verify Code", "DtoPinResetVerify -> String",
                    "{\n  \"cardNumber\": \"...\",\n  \"email\": \"...\",\n  \"code\": \"...\"\n}", font);
            addRow(t2, "POST /card/pin/reset/confirm", "Set PIN", "DtoPinResetConfirm -> String",
                    "{\n  \"cardNumber\": \"...\",\n  \"email\": \"...\",\n  \"newPin\": \"...\"\n}", font);

            document.add(t2);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=API_Documentation.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private void addRow(Table table, String endpoint, String description, String usage, String json, PdfFont font) {
        table.addCell(new Paragraph(endpoint).setFontSize(9));
        table.addCell(new Paragraph(description).setFontSize(9));
        table.addCell(new Paragraph(usage).setFontSize(9));
        table.addCell(new Paragraph(json).setFontSize(8).setFont(font));
    }
}
