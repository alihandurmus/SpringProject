package payroll.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import payroll.exception.ResourceNotFoundException;
import payroll.exception.ValidationException;
import payroll.model.Employee;
import payroll.model.Order;
import payroll.repository.EmployeeRepository;
import payroll.model.EmployeeDTO;
import payroll.repository.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    @Autowired
    private EmployeeRepository employeeRepository;// Autowired daha az kod esneklik fakat final kullanman gerekiyosa contructor injection.
    @Autowired
    private OrderRepository orderRepository;
    @Cacheable(value = "employees")
    public Page<EmployeeDTO> getEmployees(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new ValidationException("Page request is null");
        }
        return employeeRepository.findAll(pageRequest)
                .map(EmployeeMapper.INSTANCE::toDTO);

    }
    @Cacheable(value = "employees",key = "#id")
    public Optional<EmployeeDTO> getOneEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found");
        }
        return employeeRepository.findById(id)
                .map(EmployeeMapper.INSTANCE::toDTO);
    }
    @CachePut(value = "employes",key = "#employeeDTO.id")
    public EmployeeDTO saveEmployee(@Valid EmployeeDTO employeeDTO) {
        if(employeeDTO.getFirstname()==null || employeeDTO.getLastname()==null){
            throw new ValidationException("Firstname and Lastname must not be null");
        }
        Employee employee = EmployeeMapper.INSTANCE.toEntity(employeeDTO);
        List<Order> orders = orderRepository.findByRole(employee.getRole());
        employee.setOrders(orders);
        for (Order order : orders) {
            order.getEmployees().add(employee);
            orderRepository.save(order);
        }
        employee = employeeRepository.save(employee);
        return EmployeeMapper.INSTANCE.toDTO(employee);
    }
    @CachePut(value = "employees",key = "#id")
    public EmployeeDTO updateEmployee(Long id,@Valid EmployeeDTO newEmployeeDTO) {
        if(!employeeRepository.existsById(id)){
            throw new ResourceNotFoundException("Employee not found");
        }
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setFirstname(newEmployeeDTO.getFirstname());
                    employee.setLastname(newEmployeeDTO.getLastname());
                    employee.setRole(newEmployeeDTO.getRole());
                    return EmployeeMapper.INSTANCE.toDTO(employeeRepository.save(employee));
                }).orElseGet(() -> {
                    newEmployeeDTO.setId(id);
                    Employee newEmployee = EmployeeMapper.INSTANCE.toEntity(newEmployeeDTO);
                    return EmployeeMapper.INSTANCE.toDTO(employeeRepository.save(newEmployee));
                });
    }
    //@CacheEvict(value = "employees",key = "#id")
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    public List<EmployeeDTO> getEmployeeByRole(String role){
        if(role==null || role.isEmpty()){
            throw new ValidationException("Role must not be null");
        }
        log.info("Fetching employees with role: {}", role);
        List<Employee> employees = employeeRepository.findByRole(role);
        log.info("Found {} employees with role {}", employees.size(), role);
        return employees.stream()
                .map(EmployeeMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    /*private EmployeeDTO convertToDTO(Employee employee) {//Bunların yerine EmployeeMapper kullandık mapstruct böylece mapper tarafı farklı bir classta daha modüler bir şekilde oldu kodumuz daha temiz oldu.
        return new EmployeeDTO(
                employee.getId(),
                employee.getFirstname(),
                employee.getLastname(),
                employee.getRole()
        );
    }

    private Employee convertToEntity(EmployeeDTO employeeDTO) {
        return new Employee(
                employeeDTO.getId(),
                employeeDTO.getFirstname(),
                employeeDTO.getLastname(),
                employeeDTO.getRole()
        );
    }*/
}
