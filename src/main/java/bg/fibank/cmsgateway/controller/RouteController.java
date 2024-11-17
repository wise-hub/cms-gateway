package bg.fibank.cmsgateway.controller;

import bg.fibank.cmsgateway.model.Route;
import bg.fibank.cmsgateway.service.RouteService;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gateway")
public class RouteController {

    private final ApplicationEventPublisher publisher;
    private final RouteService routeService;

    public RouteController(ApplicationEventPublisher publisher, RouteService routeService) {
        this.publisher = publisher;
        this.routeService = routeService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshRoutes() {
        publisher.publishEvent(new RefreshRoutesEvent(this));
        return ResponseEntity.ok("Routes refreshed successfully");
    }

    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getRoutes() {
        List<Route> routes = routeService.getLoadedRoutesAsObjects();
        return ResponseEntity.ok(routes);
    }
}
