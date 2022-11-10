package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Brewery;
import guru.sfg.brewery.security.perms.BreweryReadPermission;
import guru.sfg.brewery.services.BreweryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BreweryRestController {

    private final BreweryService breweryService;

    @BreweryReadPermission
    @GetMapping("/api/v1/breweries")
    public @ResponseBody
    List<Brewery> getBreweriesJson(){
        return breweryService.getAllBreweries();
    }
}
