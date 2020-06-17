package pl.agh.order.management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.order.management.entity.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private Long shoppingCardId;
    private LocalDate shipDate;
    private Order.Status status;
    private String address;
    private Map<String, Object> shoppingCard = new HashMap<>();
    private List<Map<String, Object>> transaction = new ArrayList<>();

    public OrderResponseDTO(Order order, Map<String, Object> shoppingCard, List<Map<String, Object>> transaction) {
        id = order.getId();
        shoppingCardId = order.getShoppingCardId();
        shipDate = order.getShipDate();
        status = order.getStatus();
        address = order.getAddress();
        this.shoppingCard = shoppingCard;
        this.transaction = transaction;
    }
}
