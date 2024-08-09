package payroll.controller;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.exception.EmployeeNotFoundException;
import payroll.model.Employee;
import payroll.model.EmployeeDTO;
import payroll.model.Order;
import payroll.exception.OrderNotFoundException;
import payroll.repository.EmployeeRepository;
import payroll.service.KafkaProducerClass;
import payroll.service.OrderService;
import payroll.enums.Status;

@RequestMapping("/orders")
@RestController
public class OrderController {
    private final OrderService orderService;
    private final OrderModelAssembler orderModelAssembler;
    private final EmployeeRepository employeeRepository;
    private final KafkaProducerClass kafkaProducerClass;

    @Autowired
    public OrderController(OrderService orderService, OrderModelAssembler orderModelAssembler, EmployeeRepository employeeRepository,KafkaProducerClass kafkaProducerClass) {
        this.orderService = orderService;
        this.orderModelAssembler = orderModelAssembler;
        this.employeeRepository = employeeRepository;
        this.kafkaProducerClass = kafkaProducerClass;
    }

    @GetMapping()
    public CollectionModel<EntityModel<Order>> all() {

        List<EntityModel<Order>> orders = orderService.getOrders().stream()
                .map(orderModelAssembler::toModel)
                .collect(Collectors.toList());
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:GET,status:Success,message:Order GET request successfully,timestamp:"+milistime);
        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Order> one(@PathVariable Long id) {
        Order order = orderService.getOneOrder(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:GET,status:Success,message:Order GET one request successfully,timestamp:"+milistime);
        return orderModelAssembler.toModel(order);
    }

    @PostMapping()
    ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {//Sadece String isteme gibi

        order.setStatus(Status.IN_PROGRESS);
        Order newOrder = orderService.saveOrder(order);
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:POST,status:Success,message:Employee save process success,timestamp:"+milistime);
        return ResponseEntity
                .created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
                .body(orderModelAssembler.toModel(newOrder));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {

        Order order = orderService.getOneOrder(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELED);
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:DELETE,status:Success,message:Order cancel process success,timestamp:"+milistime);
            return ResponseEntity.ok(orderModelAssembler.toModel(orderService.saveOrder(order)));
        }
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:DELETE,status:Fail,message:Order cancel process fail,timestamp:"+milistime);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {
        Order order = orderService.getOneOrder(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:PUT,status:Success,message:Order complete process success,timestamp:"+milistime);
            return ResponseEntity.ok(orderModelAssembler.toModel(orderService.saveOrder(order)));
        }
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:PUT,status:Fail,message:Order complete process fail,timestamp:"+milistime);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
    }

    @GetMapping("/{id}/employees")
    public List<Employee> getOrdersByEmployeeId(@PathVariable Long id) {//dto
        Order order = orderService.getOneOrder(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:GET,status:Success,message:Order get orders by employee id process successfully,timestamp:"+milistime);
        return employeeRepository.findByRole(order.getRole());
    }

    @GetMapping("/role/{role}")
    public List<Order> getOrdersByRole(String role) {
        List orders = orderService.getOrderByRole(role);
        Long milistime = System.currentTimeMillis();
        kafkaProducerClass.send("method:GET,status:Success,message:Order get orders by role process successfully,timestamp:"+milistime);
        return orders;

    }


}
