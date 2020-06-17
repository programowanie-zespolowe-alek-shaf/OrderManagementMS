package pl.agh.order.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.agh.order.management.dto.ListResponse;
import pl.agh.order.management.dto.OrderDTO;
import pl.agh.order.management.dto.OrderResponseDTO;
import pl.agh.order.management.entity.Order;
import pl.agh.order.management.repository.OrderRepository;
import pl.agh.order.management.rest.MicroService;
import pl.agh.order.management.rest.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @MockBean
    private RestClient restClient;

    private final Map<String, Object> shoppingCardOne = ImmutableMap.of("id", 1, "username", "mark");
    private final Map<String, Object> shoppingCardTwo = ImmutableMap.of("id", 2, "username", "john");
    private final Map<String, Object> shoppingCardThree = ImmutableMap.of("id", 3, "username", "kate");

    private final List<Map<String, Object>> transactionOne = List.of(ImmutableMap.of("id", 1, "amount", 123.3));
    private final List<Map<String, Object>> transactionTwo = List.of(ImmutableMap.of("id", 2, "amount", 543.1));
    private final List<Map<String, Object>> transactionThree = List.of(ImmutableMap.of("id", 3, "amount", 772.2));

    @BeforeEach
    void setUp() {
        Mockito.when(restClient.get(MicroService.CART_MS, "/shoppingCards/1", Map.class)).thenReturn(shoppingCardOne);
        Mockito.when(restClient.get(MicroService.CART_MS, "/shoppingCards/2", Map.class)).thenReturn(shoppingCardTwo);
        Mockito.when(restClient.get(MicroService.CART_MS, "/shoppingCards/3", Map.class)).thenReturn(shoppingCardThree);

        Mockito.when(restClient.get(MicroService.PAYMENT_MS, "/transaction/shoppingCardID/1", List.class)).thenReturn(transactionOne);
        Mockito.when(restClient.get(MicroService.PAYMENT_MS, "/transaction/shoppingCardID/2", List.class)).thenReturn(transactionTwo);
        Mockito.when(restClient.get(MicroService.PAYMENT_MS, "/transaction/shoppingCardID/3", List.class)).thenReturn(transactionThree);
    }

    @Test
    void getAllOrders() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/orders")
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ListResponse ordersList = objectMapper.readValue(response.getContentAsString(), ListResponse.class);
        Order[] orders = objectMapper.readValue(objectMapper.writeValueAsString(ordersList.getList()), Order[].class);
        assertThat(asList(orders)).containsExactlyInAnyOrderElementsOf(orderRepository.findAll());
        OrderResponseDTO[] orderResponseDTOS = objectMapper.readValue(objectMapper.writeValueAsString(ordersList.getList()), OrderResponseDTO[].class);

        //noinspection unchecked
        assertThat(asList(orderResponseDTOS))
                .extracting(OrderResponseDTO::getShoppingCard)
                .containsExactlyInAnyOrder(shoppingCardOne, shoppingCardTwo, shoppingCardThree);

        //noinspection unchecked
        assertThat(asList(orderResponseDTOS))
                .extracting(OrderResponseDTO::getTransaction)
                .containsExactlyInAnyOrder(transactionOne, transactionTwo, transactionThree);
    }

    @Test
    void getAllOrdersForUser() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/orders")
                .param("username", "john")
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ListResponse ordersList = objectMapper.readValue(response.getContentAsString(), ListResponse.class);
        Order[] orders = objectMapper.readValue(objectMapper.writeValueAsString(ordersList.getList()), Order[].class);
        assertThat(asList(orders)).containsExactly(orderRepository.findById(2L).get());
        OrderResponseDTO[] orderResponseDTOS = objectMapper.readValue(objectMapper.writeValueAsString(ordersList.getList()), OrderResponseDTO[].class);

        //noinspection unchecked
        assertThat(asList(orderResponseDTOS)).extracting(OrderResponseDTO::getShoppingCard).containsExactly(shoppingCardTwo);

        //noinspection unchecked
        assertThat(asList(orderResponseDTOS)).extracting(OrderResponseDTO::getTransaction).containsExactly(transactionTwo);
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

        OrderResponseDTO orderResponseDTO = objectMapper.readValue(response.getContentAsString(), OrderResponseDTO.class);
        assertThat(orderResponseDTO).extracting(OrderResponseDTO::getShoppingCard).isEqualTo(shoppingCardTwo);
        assertThat(orderResponseDTO).extracting(OrderResponseDTO::getTransaction).isEqualTo(transactionTwo);
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

        OrderResponseDTO orderResponseDTO = objectMapper.readValue(response.getContentAsString(), OrderResponseDTO.class);
        assertThat(orderResponseDTO).extracting(OrderResponseDTO::getShoppingCard).isEqualTo(shoppingCardOne);
        assertThat(orderResponseDTO).extracting(OrderResponseDTO::getTransaction).isEqualTo(transactionOne);
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
    void addOrderWithNullAddress() throws Exception {

        OrderDTO orderDTO = getOrderDTO();
        orderDTO.setAddress(null);

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("address=[null] -> must not be null"));
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

        OrderResponseDTO orderResponseDTO = objectMapper.readValue(response.getContentAsString(), OrderResponseDTO.class);
        assertThat(orderResponseDTO).extracting(OrderResponseDTO::getShoppingCard).isEqualTo(shoppingCardOne);
        assertThat(orderResponseDTO).extracting(OrderResponseDTO::getTransaction).isEqualTo(transactionOne);
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
        orderDTO.setShipDate(LocalDate.now());
        orderDTO.setAddress("Cecilia Chapman 711-2880 Nulla St. Mankato Mississippi 96522");
        return orderDTO;
    }
}