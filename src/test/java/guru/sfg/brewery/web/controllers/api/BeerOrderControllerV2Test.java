package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void listOrdersNotAuth() throws Exception {
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
    void listOrdersCustomerDunedinAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    @WithUserDetails("spring")
    void getByOrderIdAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @Test
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    void getByOrderIdCustomerNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().isNotFound());
    }
}