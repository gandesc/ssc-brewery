package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.security.perms.OrderCreatePermission;
import guru.sfg.brewery.security.perms.OrderPickupPermission;
import guru.sfg.brewery.security.perms.OrderReadPermission;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private final BeerOrderService beerOrderService;

    public BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @GetMapping("orders")
    @OrderReadPermission
    public BeerOrderPagedList listOrders(
            @PathVariable("customerId") UUID customerId,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    @OrderCreatePermission
    public BeerOrderDto placeOrder(
            @PathVariable("customerId") UUID customerId,
            @RequestBody BeerOrderDto beerOrderDto
    ) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @GetMapping("orders/{orderId}")
    @OrderReadPermission
    public BeerOrderDto getOrder(
            @PathVariable("customerId") UUID customerId,
            @PathVariable("orderId") UUID orderId
    ) {
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OrderPickupPermission
    public void pickupOrder(
            @PathVariable("customerId") UUID customerId,
            @PathVariable("orderId") UUID orderId
    ) {
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
