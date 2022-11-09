package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweryControllerIT extends BaseIT {

    @Test
    public void listBreweriesHttpBasicCUSTOMER() throws Exception{
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    public void listBreweriesHttpBasicUSER() throws Exception{
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void listBreweriesNOAUTH() throws Exception{
        mockMvc.perform(get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getBreweriesHttpBasicCUSTOMER() throws Exception{
        mockMvc.perform(get("/api/v1/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    public void getBreweriesHttpBasicUSER() throws Exception{
        mockMvc.perform(get("/api/v1/breweries")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getBreweriesNOAUTH() throws Exception{
        mockMvc.perform(get("/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }
}
