package pl.agh.order.management.dto;

import lombok.Data;
import pl.agh.order.management.entity.Order;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class OrderPutDTO {
    @NotNull
    private Long shoppingCardId;
    @NotNull
    private LocalDate shipDate;
    @NotNull
    private Order.Status status = Order.Status.PLACED;
    @NotNull
    private String address;

    public Order toEntity() {
        return Order.builder()
                .shoppingCardId(shoppingCardId)
                .shipDate(shipDate)
                .status(status)
                .address(address)
                .build();
    }
}
