package payroll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payroll.model.File;
import payroll.service.FileStorageService;
import payroll.service.KafkaProducerClass;
import payroll.service.PdfReportService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private KafkaProducerClass kafkaProducerClass;
    @Autowired
    private PdfReportService pdfReportService;

    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File uploadedFile = fileStorageService.storeFile(file);
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:POST,status:Success,message:File uploaded successfully,timestamp:" + milistime);
            return new ResponseEntity<>(uploadedFile, HttpStatus.OK);
        } catch (IOException e) {
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:POST,status:Fail,message:File upload fail,timestamp:" + milistime);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        File dbFile = fileStorageService.getFile(id);
        if (dbFile != null) {
            try {
                Path path = Paths.get(dbFile.getFilePath());
                byte[] data = Files.readAllBytes(path);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFilename() + "\"");
                headers.add(HttpHeaders.CONTENT_TYPE, dbFile.getFileType());
                Long milistime = System.currentTimeMillis();
                kafkaProducerClass.send("method:GET,status:Success,message:File downloaded successfully,timestamp:" + milistime);
                return new ResponseEntity<>(data, headers, HttpStatus.OK);

            } catch (IOException e) {
                Long milistime = System.currentTimeMillis();
                kafkaProducerClass.send("method:GET,status:Fail,message:File download fail,timestamp:" + milistime);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }


        } else {
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:GET,status:Fail,message:File download fail,timestamp:" + milistime);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/report")
    public ResponseEntity<InputStreamResource> generateReport(@RequestParam String startDate,@RequestParam String endDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = simpleDateFormat.parse(startDate);
        Date end = simpleDateFormat.parse(endDate);
        ByteArrayInputStream bis = pdfReportService.generateReport(start,end);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=request_stats.pdf");
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:GET,status:Fail,message:File download fail,timestamp:"+milistime);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));

    }

}
