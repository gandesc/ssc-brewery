package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class CustomerControllerIT extends BaseIT {

    @DisplayName("Get Customers")
    @Nested
    class GetCustomers {

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.CustomerControllerIT#getStreamNotUser")
        void getCustomersAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/customers").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }

        @Test
        void getCustomersNotAuth() throws Exception {
            mockMvc.perform(get("/customers")
                            .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getCustomersNotLoggedIn() throws Exception {
            mockMvc.perform(get("/customers"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Create Customer")
    @Nested
    class CreateCustomer {

        @ParameterizedTest(name = "{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.CustomerControllerIT#getStreamNotAdmin")
        void createCustomerNotAuth(String user, String pwd) throws Exception {
            mockMvc.perform(post("/customers/new").with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void createCustomerAuth() throws Exception {
            mockMvc.perform(post("/customers/new").with(httpBasic("spring", "guru")))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        void createCustomerNotLoggedIn() throws Exception {
            mockMvc.perform(post("/customers/new"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
