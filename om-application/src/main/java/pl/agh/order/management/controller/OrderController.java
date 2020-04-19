package pl.agh.order.management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.agh.order.management.dto.OrderDTO;
import pl.agh.order.management.entity.Order;
import pl.agh.order.management.service.OrderService;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOrder(@RequestBody @Valid OrderDTO order) {
        Order createdOrder = orderService.add(order);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(createdOrder);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOrder(@PathVariable("id") Long id) {
        Order order = orderService.find(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(order);
        }
    }

    @PutMapping(value = "{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOrder(@PathVariable("id") Long id, @RequestBody @Valid OrderDTO orderDTO) {
        Order updatedOrder = orderService.update(id, orderDTO);
        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedOrder);
        }
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        Order deletedOrder = orderService.delete(id);
        if (deletedOrder == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
