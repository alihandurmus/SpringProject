package payroll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payroll.exception.ApiExceptionHandler;
import payroll.exception.ApiRequestException;
import payroll.exception.ResourceNotFoundException;
import payroll.exception.ValidationException;
import payroll.model.Employee;
import payroll.model.EmployeeDTO;
import payroll.model.Order;
import payroll.repository.EmployeeRepository;
import payroll.repository.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private KafkaProducerClass kafkaProducerClass;

    public List<Order> getOrders() {
        List orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:GET,status:Fail,message:Order get process fail,timestamp:"+milistime);
            throw new ValidationException("Page request is null");
        }
        return orders;
    }

    public Optional<Order> getOneOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:GET,status:Fail,message:Order get one process fail,timestamp:"+milistime);
            throw new ResourceNotFoundException("Order not found");
        }
        return orderRepository.findById(id);
    }

    public Order saveOrder(Order order) {
        try {
            List<Employee> employees = employeeRepository.findByRole(order.getRole());
            order.setEmployees(employees);
            for (Employee employee : employees) {
                employee.getOrders().add(order);
                employeeRepository.save(employee);
            }
            return orderRepository.save(order);
        }catch (Exception e) {
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:POST,status:Fail,message:Order save process fail,timestamp:"+milistime);
            return null;
        }

    }

    public List<Order> getOrderByRole(String role) {
        List orders = orderRepository.findByRole(role);
        if (orders.isEmpty()) {
            Long milistime = System.currentTimeMillis();
            kafkaProducerClass.send("method:GET,status:Fail,message:Order get order by role process fail,timestamp:"+milistime);
            throw new ResourceNotFoundException("Order not found");
        }
        return orders;
    }
   /* public Order cancelOrder(Long id,Order newOrder){
        return orderRepository.findById(id)
                .map(order -> {
                    order.setDescription(newOrder.getDescription());
                    order.setStatus(newOrder.getStatus());
                    return orderRepository.save(order);
                }).orElseGet(()->{
                    newOrder.setId(id);
                    return orderRepository.save(newOrder);
                });
    }
    public void completeOrder(Long id){
        orderRepository.deleteById(id);
    }*/
}
