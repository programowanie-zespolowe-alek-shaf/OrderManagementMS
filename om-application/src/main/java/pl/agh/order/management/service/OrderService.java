package pl.agh.order.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pl.agh.order.management.dto.ListResponse;
import pl.agh.order.management.dto.ListUtil;
import pl.agh.order.management.dto.OrderDTO;
import pl.agh.order.management.dto.OrderResponseDTO;
import pl.agh.order.management.entity.Order;
import pl.agh.order.management.repository.OrderRepository;
import pl.agh.order.management.rest.MicroService;
import pl.agh.order.management.rest.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestClient restClient;

    public OrderResponseDTO find(Long id) {
        return getOrderResponse(orderRepository.findById(id).orElse(null));
    }

    public ListResponse findAll(int limit, int offset, String username) {
        List<OrderResponseDTO> orders = orderRepository.findAll().stream().map(this::getOrderResponse).collect(Collectors.toList());
        if (username != null) {
            orders = orders.stream().filter(e -> username.equals(e.getShoppingCard().get("username"))).collect(Collectors.toList());
        }
        int count = orders.size();
        orders = ListUtil.clampedSublist(orders, limit, offset);
        return new ListResponse(orders, count);
    }

    public OrderResponseDTO add(OrderDTO order) {
        return getOrderResponse(orderRepository.save(order.toEntity()));
    }

    public OrderResponseDTO update(Long id, OrderDTO orderDTO) {
        if (!orderRepository.existsById(id)) {
            return null;
        }

        Order order = orderDTO.toEntity();
        order.setId(id);
        return getOrderResponse(orderRepository.save(order));
    }

    public OrderResponseDTO delete(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            return null;
        }
        Order order = orderOptional.get();
        orderRepository.delete(order);
        return getOrderResponse(order);
    }

    private OrderResponseDTO getOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        Long shoppingCardId = order.getShoppingCardId();
        Map<String, Object> shoppingCard = getShoppingCard(shoppingCardId);
        var transactions = getTransactions(shoppingCardId);
        return new OrderResponseDTO(order, shoppingCard, transactions);
    }

    private Map<String, Object> getShoppingCard(Long id) {
        try {
            //noinspection unchecked
            return restClient.get(MicroService.CART_MS, "/shoppingCards/" + id, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    private List<Map<String, Object>> getTransactions(Long shoppingCardId) {
        try {
            //noinspection unchecked
            return restClient.get(MicroService.PAYMENT_MS, "/transaction/shoppingCardID/" + shoppingCardId, List.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

}
