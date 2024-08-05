package payroll.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import payroll.enums.Status;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMER_ORDER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private @Id
    @GeneratedValue Long id;
    private String description;
    private Status status;
    private String role;


    @ManyToMany(mappedBy = "orders", fetch = FetchType.EAGER)
    //@JsonBackReference
    private List<Employee> employees = new ArrayList<>();

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
