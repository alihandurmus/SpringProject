package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import payroll.model.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {//Etki alanı Employee sınıfı id si Long türünde olduğu için

    List<Employee> findByRole(String role);
}
