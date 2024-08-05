package payroll.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import payroll.model.EmployeeDTO;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<EmployeeDTO, EntityModel<EmployeeDTO>> {

    @Override
    public EntityModel<EmployeeDTO> toModel(EmployeeDTO employee) {
        return EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).all(0,5)).withRel("employees"));
    }
}
