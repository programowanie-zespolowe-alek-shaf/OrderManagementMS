package pl.agh.order.management.dto;

import lombok.Data;
import pl.agh.order.management.entity.Order;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class OrderDTO {
    @NotNull
    private Long shoppingCardId;
    @NotNull
    @FutureOrPresent
    private LocalDate shipDate;
    private Order.Status status = Order.Status.PLACED;

    public Order toEntity() {
        return Order.builder()
                .shoppingCardId(shoppingCardId)
                .shipDate(shipDate)
                .status(status)
                .build();
    }
}
