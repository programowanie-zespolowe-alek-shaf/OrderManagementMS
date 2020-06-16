package pl.agh.order.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.agh.order.management.dto.ListResponse;
import pl.agh.order.management.dto.ListUtil;
import pl.agh.order.management.dto.OrderDTO;
import pl.agh.order.management.entity.Order;
import pl.agh.order.management.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order find(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public ListResponse findAll(int limit, int offset) {
        List<Order> orders = orderRepository.findAll();
        orders = ListUtil.clampedSublist(orders, limit, offset);
        return new ListResponse(orders, orders.size());
    }

    public Order add(OrderDTO order) {
        return orderRepository.save(order.toEntity());
    }

    public Order update(Long id, OrderDTO orderDTO) {
        if (!orderRepository.existsById(id)) {
            return null;
        }

        Order order = orderDTO.toEntity();
        order.setId(id);
        return orderRepository.save(order);
    }

    public Order delete(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            return null;
        }
        Order order = orderOptional.get();
        orderRepository.delete(order);
        return order;
    }
}
