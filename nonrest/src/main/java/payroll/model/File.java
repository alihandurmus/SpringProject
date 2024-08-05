package payroll.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "files")
public class File{
    @Id
    @GeneratedValue
    private Long id;
    private String filename;
    private String fileType;
    private String filePath;//Yereldeki Dosya yolu
}
