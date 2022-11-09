package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Test
    public void getBreweriesHttpBasicCustomerRole() throws Exception{
        mockMvc.perform(get("/api/v1/breweries")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    public void getBreweriesHttpBasicUserCustomerRole() throws Exception{
        mockMvc.perform(get("/api/v1/breweries")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getBreweriesUnauthenticated() throws Exception{
        mockMvc.perform(get("/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void deleteBeerHttpBasicUserRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteBeerHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }
}
