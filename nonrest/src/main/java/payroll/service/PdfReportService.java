package payroll.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfReportService {

    @Autowired
    private StaticsService staticsService;

    public ByteArrayInputStream generateReport(Date startDate, Date endDate) {

        List<StaticsService.StatisticsResult> stats = staticsService.getHttpRequestStatistics(startDate,endDate);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("HTTP Request Statistics Report"));
            document.add(new Paragraph("Date Range: " + new SimpleDateFormat("yyyy-MM-dd").format(startDate) +
                    " - " + new SimpleDateFormat("yyyy-MM-dd").format(endDate)));

            PdfPTable table = new PdfPTable(9); // 1 column for date + 8 columns for methods (4 methods * 2 status)
            table.setWidthPercentage(100);

            // Table header
            table.addCell("Date");
            table.addCell("GET Success");
            table.addCell("GET Fail");
            table.addCell("POST Success");
            table.addCell("POST Fail");
            table.addCell("PUT Success");
            table.addCell("PUT Fail");
            table.addCell("DELETE Success");
            table.addCell("DELETE Fail");

            for (StaticsService.StatisticsResult result : stats) {
                table.addCell(result.getDate());
                table.addCell(getCount(result.getCounts(), "GET", "Success"));
                table.addCell(getCount(result.getCounts(), "GET", "Fail"));
                table.addCell(getCount(result.getCounts(), "POST", "Success"));
                table.addCell(getCount(result.getCounts(), "POST", "Fail"));
                table.addCell(getCount(result.getCounts(), "PUT", "Success"));
                table.addCell(getCount(result.getCounts(), "PUT", "Fail"));
                table.addCell(getCount(result.getCounts(), "DELETE", "Success"));
                table.addCell(getCount(result.getCounts(), "DELETE", "Fail"));
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String getCount(List<StaticsService.StatisticsResult.CountByMethodStatus> counts, String method, String status) {
        return counts.stream()
                .filter(count -> count.getMethod().equals(method) && count.getStatus().equals(status))
                .map(count -> String.valueOf(count.getCount()))
                .findFirst()
                .orElse("0");
    }
}
