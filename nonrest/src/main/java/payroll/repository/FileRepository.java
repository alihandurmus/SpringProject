package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import payroll.model.File;
@Repository
public interface FileRepository extends JpaRepository<File,Long> {
}
