package payroll.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import payroll.exception.ResourceNotFoundException;
import payroll.model.EmployeeDTO;
import payroll.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.stream.Collectors;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeService employeeService;
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEmployeesTest_ShouldReturnEmployeeList() {//unit test naming
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<EmployeeDTO> expectedEmployeesList = List.of(
                new EmployeeDTO(1L,"Alihan","Durmuş","Phone"),
                new EmployeeDTO(2L,"Alperen","Öztürk","Television")

        );
        Page<EmployeeDTO> expectedEmployees = new PageImpl<>(expectedEmployeesList);
        when(employeeRepository.findAll(pageRequest)).thenReturn(
                new PageImpl<>(expectedEmployees.stream().map(EmployeeMapper.INSTANCE::toEntity).collect(Collectors.toList()))
        );

        Page<EmployeeDTO> actualEmployees = employeeService.getEmployees(pageRequest);
        List<EmployeeDTO> actualEmployeesList = actualEmployees.getContent();

        assertEquals(expectedEmployeesList, actualEmployeesList);//field field karşılaştırma


    }
    @Test
    void getOneEmployeeTest_ShouldReturnEmployee() {
        Long employeeId = 1L;
        EmployeeDTO expectedEmployee = new EmployeeDTO(
                employeeId,
                "Alihan",
                "Durmuş",
                "Phone"
        );
        when(employeeRepository.findById(employeeId)).thenReturn(
                Optional.of(EmployeeMapper.INSTANCE.toEntity(expectedEmployee))
        );
        Optional<EmployeeDTO> actualEmployeeOptional = employeeService.getOneEmployee(employeeId);
        assertTrue(actualEmployeeOptional.isPresent(), "Employee should be present");
        EmployeeDTO actualEmployee = actualEmployeeOptional.get();
        assertEquals(expectedEmployee, actualEmployee);



    }
    @Test
    void getOneEmployee_ShouldThrowResourceNotFoundException() {
        Long employeeId = 1L;
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getOneEmployee(employeeId));
    }
    //@Test
    /*void deneme(){
        EmployeeDTO employeeDTO = new EmployeeDTO(
                null,
                "Tanrıverdi",
                "Role",
                "role"

        );
        EmployeeDTO employeeDTO1 = new EmployeeDTO(
                null,
                "Tanrıverdi",
                "Role",
                "role"

        );
        assertEquals(employeeDTO1,employeeDTO);
    }*/
}
