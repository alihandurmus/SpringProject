package payroll.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;

import org.springframework.hateoas.EntityModel;
import payroll.exception.EmployeeNotFoundException;
import payroll.exception.ResourceNotFoundException;
import payroll.model.EmployeeDTO;
import payroll.model.Order;
import payroll.repository.OrderRepository;
import payroll.service.EmployeeService;
import payroll.service.KafkaProducer;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
//@Api(value = "Employee Management System")
@Validated
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeModelAssembler assembler;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private KafkaProducer kafkaProducer;

    //@ApiOperation(value = "Bütün çalışanları listele",response = List.class)
    @GetMapping()
    @Operation(summary = "Çalışanları getir",description = "Bu metod çalışanları getirir.")
    public
//Get methodu ile bütün employeeleri getir.
    CollectionModel<EntityModel<EmployeeDTO>> all(
            @RequestParam int page,
            @RequestParam int size
    ) {
        PageRequest pr = PageRequest.of(page, size);
        List<EntityModel<EmployeeDTO>> employees = employeeService.getEmployees(pr).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if(employees==null){
            throw new ResourceNotFoundException("Resource not found");
        }

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all(page,size)).withSelfRel());

    }

    //@ApiOperation(value = "Yeni çalışan ekle")
    @PostMapping()
//Post metodu ile yeni employee ekleme
    ResponseEntity<?> newEmployee(
            //@ApiParam(value = "Yeni eklenee çalışan objesi")
            @Valid @RequestBody EmployeeDTO newEmployee) {
        EntityModel<EmployeeDTO> entityModel = assembler.toModel(employeeService.saveEmployee(newEmployee));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    //@ApiOperation(value = "Id sine göre sadece ilgili çalışani göster.")
    @GetMapping("/{id}")
    public
//Get metodu ile id sine göre employee getir. Eğer öyle bir employee yoksa throw
    EntityModel<EmployeeDTO> one(
            //@ApiParam(value = "Gösterilecek çalışanın id'si")
            @PathVariable Long id) {
        EmployeeDTO employee = employeeService.getOneEmployee(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }

    //@ApiOperation(value = "Id'sine göre çalışanı güncelle")
    @PutMapping("/{id}")
    ResponseEntity<?> replaceEmployee(
            //@ApiParam(value = "Güncellenecek çalışan DTO objesi")
            @Valid @RequestBody EmployeeDTO newEmployee,
            //@ApiParam(value = "Güncellenecek çalışan id'si")
            @PathVariable Long id) {

        EntityModel<EmployeeDTO> entityModel = assembler.toModel(employeeService.updateEmployee(id, newEmployee));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    //@ApiOperation(value = "Id'sine göre çalışan sil")
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteEmployee(
            //@ApiParam(value = "Silinecek çalışan id'si")
            @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/orders")
    public List<Order> getOrdersByEmployeeId(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getOneEmployee(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return orderRepository.findByRole(employee.getRole());
    }

    @GetMapping("/role/{role}")
    public List<EmployeeDTO> getEmployeesByRole(@PathVariable String role) {
        return employeeService.getEmployeeByRole(role);

    }


}
