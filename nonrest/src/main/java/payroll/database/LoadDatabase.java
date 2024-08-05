package payroll.database;

import com.github.javafaker.Faker;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import payroll.enums.Status;
import payroll.model.Employee;
import payroll.model.Order;
import payroll.repository.EmployeeRepository;
import payroll.repository.OrderRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration

public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
        return args -> {
            Faker faker = new Faker();
            List<Employee> employees = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
           /* Employee employee1 = new Employee(null, "Alihan", "Durmuş", "Phone", null);
            Employee employee2 = new Employee(null, "Murat", "Alpöz", "Phone", null);
            Employee employee3 = new Employee(null, "Ahmet", "Yılmaz", "Television", null);
            Employee employee4 = new Employee(null, "Mehmet", "Kaya", "Television", null);
            log.info("Preloading {}", employeeRepository.save(employee1));
            log.info("Preloading {}", employeeRepository.save(employee2));
            log.info("Preloading {}", employeeRepository.save(employee3));
            log.info("Preloading {}", employeeRepository.save(employee4));

            Order order1 = new Order(null, "Samsung Galaxy S23", Status.COMPLETED, "Phone", null);
            Order order2 = new Order(null, "IPhone X", Status.IN_PROGRESS, "Phone", null);
            Order order3 = new Order(null, "Samsung TV", Status.COMPLETED, "Television", null);
            Order order4 = new Order(null, "LG TV", Status.IN_PROGRESS, "Television", null);

            log.info("Preloading {}", orderRepository.save(order1));
            log.info("Preloading {}", orderRepository.save(order2));
            log.info("Preloading {}", orderRepository.save(order3));
            log.info("Preloading {}", orderRepository.save(order4));

            List<Employee> employees = employeeRepository.findAll();
            List<Order> orders = orderRepository.findAll();*/

            for(int i = 0; i < 50; i++) {
                Employee employee = new Employee();
                employee.setFirstname(faker.name().firstName());
                employee.setLastname(faker.name().lastName());
                employee.setRole(i % 2 == 0 ? "Phone" : "Television"); // Alternate roles
                employees.add(employee);

                log.info("Preloading {}", employeeRepository.save(employee));
            }
            for (int i = 0; i < 50; i++) {
                Order order = new Order();
                order.setDescription(faker.commerce().productName());
                order.setStatus(i % 2 == 0 ? Status.COMPLETED : Status.IN_PROGRESS); // Alternate statuses
                order.setRole(i % 2 == 0 ? "Phone" : "Television"); // Alternate roles
                orders.add(order);

                log.info("Preloading {}", orderRepository.save(order));
            }

            for (Employee employee : employees) {
                for (Order order : orders) {
                    if (employee.getRole().equals(order.getRole())) {
                        employee.getOrders().add(order);
                        order.getEmployees().add(employee);
                    }
                }
            }

            employeeRepository.saveAll(employees);
            orderRepository.saveAll(orders);
            employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));


            orderRepository.findAll().forEach(order -> {
                log.info("Preloaded " + order);
            });
        };
    }

}
