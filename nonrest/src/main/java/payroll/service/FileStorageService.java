package payroll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import payroll.model.File;
import payroll.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    @Autowired
    private FileRepository fileRepository;
    private final String UPLOAD_DIR = "uploads/";

    public File storeFile(MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String filePath = UPLOAD_DIR + file.getOriginalFilename();
        Files.write(Paths.get(filePath), file.getBytes());

        File fileEntity = new File();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFilePath(filePath);
        return fileRepository.save(fileEntity);


    }

    public File getFile(Long fileId) {
        return fileRepository.findById(fileId).orElse(null);
                        //(() -> new RuntimeException("Could not find file with ID: " + fileId));
    }
}
