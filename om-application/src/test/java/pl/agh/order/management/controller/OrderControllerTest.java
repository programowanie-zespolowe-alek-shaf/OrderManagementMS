package pl.agh.order.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.agh.order.management.dto.OrderDTO;
import pl.agh.order.management.entity.Order;
import pl.agh.order.management.repository.OrderRepository;

import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:data.sql")
@WithMockUser
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllOrders() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Order[] orders = objectMapper.readValue(response.getContentAsString(), Order[].class);
        assertThat(asList(orders)).containsExactlyInAnyOrderElementsOf(orderRepository.findAll());
    }

    @Test
    void getOrder() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/orders/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Order order = objectMapper.readValue(response.getContentAsString(), Order.class);
        assertThat(order).isEqualTo(orderRepository.findById(2L).orElse(null));
    }

    @Test
    void getNonExistingOrder() throws Exception {
        mvc.perform(get("/orders/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderWithInvalidId() throws Exception {
        mvc.perform(get("/orders/asd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOrder() throws Exception {

        assertThat(orderRepository.count()).isEqualTo(3);

        MvcResult mvcResult = mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getOrderDTO())))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(orderRepository.count()).isEqualTo(4);

        MockHttpServletResponse response = mvcResult.getResponse();
        Order order = objectMapper.readValue(response.getContentAsString(), Order.class);
        assertThat(order).isEqualTo(orderRepository.findById(order.getId()).orElse(null));
    }

    @Test
    void addOrderWithInvalidDate() throws Exception {

        OrderDTO orderDTO = getOrderDTO();
        orderDTO.setShipDate(LocalDate.of(2000, 1, 1));

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("shipDate=[2000-01-01] -> must be a date in the present or in the future"));
    }

    @Test
    void addOrderWithoutShoppingCardId() throws Exception {

        OrderDTO orderDTO = getOrderDTO();
        orderDTO.setShoppingCardId(null);

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("shoppingCardId=[null] -> must not be null"));
    }

    @Test
    void updateOrder() throws Exception {

        assertThat(orderRepository.count()).isEqualTo(3);

        OrderDTO orderDTO = getOrderDTO();
        orderDTO.setStatus(Order.Status.DELIVERED);

        MvcResult mvcResult = mvc.perform(put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(orderRepository.count()).isEqualTo(3);

        MockHttpServletResponse response = mvcResult.getResponse();
        Order order = objectMapper.readValue(response.getContentAsString(), Order.class);
        assertThat(order).isEqualTo(orderRepository.findById(order.getId()).orElse(null));
    }

    @Test
    void updateOrderWithoutShipDate() throws Exception {

        OrderDTO orderDTO = getOrderDTO();
        orderDTO.setShipDate(null);

        mvc.perform(put("/orders/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("shipDate=[null] -> must not be null"));
    }

    @Test
    void updateNonExistingOrder() throws Exception {

        mvc.perform(put("/orders/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getOrderDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder() throws Exception {
        assertThat(orderRepository.count()).isEqualTo(3);

        mvc.perform(delete("/orders/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(orderRepository.count()).isEqualTo(2);
        assertThat(orderRepository.findById(2L).orElse(null)).isNull();
    }

    @Test
    void deleteNotExistingOrder() throws Exception {
        mvc.perform(delete("/orders/4"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private OrderDTO getOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setShoppingCardId(1L);
        orderDTO.setShipDate(LocalDate.of(2020, 5, 13));
        return orderDTO;
    }
}