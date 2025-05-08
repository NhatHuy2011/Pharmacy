package com.project.pharmacy.service.invoice;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenerateInvoiceService {
    public void generateInvoices() throws IOException {
        String directoryPath = "C:\\Invoice";
        String fileName = "invoices.pdf";
        String path = directoryPath + "\\" + fileName;

        // Kiểm tra và tạo thư mục nếu chưa tồn tại
        File directory = new File(directoryPath);
        if(!directory.exists()){
            directory.mkdirs();
        }

        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDocument);

        float column1 = 285f;
        float column2 = column1 + 150f;
        float columnWidth[] = {column1, column2};

        Table table = new Table(columnWidth);
        table.addCell("Invoice");

        document.add(table);
        document.close();
    }
}
