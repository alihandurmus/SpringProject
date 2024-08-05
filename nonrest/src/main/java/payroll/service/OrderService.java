package payroll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOneOrder(Long id) {
        return orderRepository.findById(id);
    }

    public Order saveOrder(Order order) {
        List<Employee> employees = employeeRepository.findByRole(order.getRole());
        order.setEmployees(employees);
        for (Employee employee : employees) {
            employee.getOrders().add(order);
            employeeRepository.save(employee);
        }
        return orderRepository.save(order);
    }

    public List<Order> getOrderByRole(String role) {
        return orderRepository.findByRole(role);
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
