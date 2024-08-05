package payroll.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import payroll.model.Employee;
@Repository
public interface PagingRepository extends PagingAndSortingRepository<Employee, Long> {


}
