package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import guru.sfg.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerOrderControllerV2Test extends BaseIT {

    private static final String API_ROOT = "/api/v2/orders/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;

    List<Beer> loadedBeers;

    @BeforeEach
    void setUp() {
        stPeteCustomer = customerRepository
                .findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTION)
                .orElseThrow();

        dunedinCustomer = customerRepository
                .findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING)
                .orElseThrow();

        keyWestCustomer = customerRepository
                .findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING)
                .orElseThrow();

        loadedBeers = beerRepository.findAll();
    }

    @Test
    void createOrderNoAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("spring")
    void createOrderUserAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    void createOrderUserAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(DefaultBreweryLoader.KEYWEST_USER)
    void createOrderUserNOTAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrderNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("spring")
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    void listOrdersCustomerNOTAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    @WithUserDetails("spring")
    void getByOrderIdAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    void getByOrderIdCustomerNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void pickUpOrderNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    @WithUserDetails("spring")
    void pickupOrderAdminUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    void pickupOrderCustomerUserAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/"  + beerOrder.getId() + "/pickup"))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    void pickupOrderCustomerUserNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() + "/orders/" + beerOrder.getId() + "/pickup"))
                .andExpect(status().isForbidden());
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        BeerOrderLineDto lineDto = BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .orderQuantity(2)
                .beerId(beerId)
                .build();

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatus(OrderStatusEnum.NEW)
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(List.of(lineDto))
                .build();
    }

}
