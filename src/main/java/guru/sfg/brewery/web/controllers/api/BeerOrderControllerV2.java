package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.security.perms.BeerOrderReadPermissionV2;
import guru.sfg.brewery.security.perms.BeerOrderCreatePermission;
import guru.sfg.brewery.security.perms.BeerOrderPickupPermission;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v2/orders/")
@RestController
public class BeerOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private final BeerOrderService beerOrderService;

    public BeerOrderControllerV2(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @GetMapping
    @BeerOrderReadPermissionV2
    public BeerOrderPagedList listOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (user.getCustomer() != null) {
            return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        } else {
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @BeerOrderCreatePermission
    public BeerOrderDto placeOrder(
            @PathVariable("customerId") UUID customerId,
            @RequestBody BeerOrderDto beerOrderDto
    ) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @GetMapping("{orderId}")
    @BeerOrderReadPermissionV2
    public BeerOrderDto getOrder(
            @PathVariable("customerId") UUID customerId,
            @PathVariable("orderId") UUID orderId
    ) {
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @BeerOrderPickupPermission
    public void pickupOrder(
            @PathVariable("customerId") UUID customerId,
            @PathVariable("orderId") UUID orderId
    ) {
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
