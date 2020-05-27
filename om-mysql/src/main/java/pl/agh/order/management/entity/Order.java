package pl.agh.order.management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user_order", schema = "orders")
public class Order implements Comparable<Order> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "shopping_card_id")
    private Long shoppingCardId;

    @NotNull
    @Column(name = "ship_date")
    private LocalDate shipDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @NotNull
    @Column(name = "address")
    private String address;

    @Override
    public int compareTo(Order o) {
        return this.id.compareTo(o.getId());
    }

    public enum Status {
        PLACED, APPROVED, DELIVERED, DISAPPROVED
    }
}
