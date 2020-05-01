package pl.agh.order.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.agh.order.management.dto.OrderDTO;
import pl.agh.order.management.entity.Order;
import pl.agh.order.management.repository.OrderRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order find(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Iterable<Order> findAll() {
        return orderRepository.findAll();
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
        if (!orderOptional.isPresent()) {
            return null;
        }
        Order order = orderOptional.get();
        orderRepository.delete(order);
        return order;
    }

    public String getCurrentUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
