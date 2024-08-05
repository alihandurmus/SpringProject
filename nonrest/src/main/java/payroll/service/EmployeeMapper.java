package payroll.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import payroll.model.Employee;
import payroll.model.EmployeeDTO;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "name", ignore = true)

//EmployeeDTO da name alanı bulunmadığı için bunu ignore ediyoruz.
    EmployeeDTO toDTO(Employee employee);

    /*@Mapping(target = "firstname", source = "firstname")//Burada mapstruct özellik eşleştirmesini otomatik olarak yapar fakat açıklayıcı olması için bu şekilde yazılabilir.
    @Mapping(target = "lastname", source = "lastname")
    @Mapping(target = "role", source = "role")*/
    @Mapping(target = "orders", ignore = true)
    Employee toEntity(EmployeeDTO employeeDTO);
}
