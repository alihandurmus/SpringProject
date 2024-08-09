package payroll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Component
public class ScheduledTask {
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private EmailService emailService;
    @Scheduled(cron = "0 50 15 * * ?") // Her gün saat 24.00 de mail atacak
    public void sendDailyReport() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date endDate = new Date(System.currentTimeMillis());
        String s = sdf.format(endDate);

        ByteArrayInputStream pdfStream = pdfReportService.generateReport(sdf.parse(s), sdf.parse(s));

        emailService.sendEmailWithAttachment(
                "alihandurmus1907@gmail.com",
                "Daily Http Request Report",
                "Daily pdf report is attached",
                pdfStream,
                "http_request_statistics_report.pdf"
        );

    }
}
