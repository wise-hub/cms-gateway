package bg.fibank.cmsgateway.service;

import bg.fibank.cmsgateway.model.Route;
import bg.fibank.cmsgateway.repository.RouteRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;

@Service
public class RouteService implements RouteDefinitionLocator {

    private final RouteRepository routeRepository;
    private final ObjectMapper objectMapper;

    public RouteService(RouteRepository routeRepository, ObjectMapper objectMapper) {
        this.routeRepository = routeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.defer(() -> {
            List<Route> routeModels = routeRepository.findAllRoutes();
            return Flux.fromIterable(routeModels.stream().map(this::convertToRouteDefinition).toList());
        });
    }

    public List<Route> getLoadedRoutesAsObjects() {
        return routeRepository.findAllRoutes();
    }

    private RouteDefinition convertToRouteDefinition(Route model) {
        try {
            return buildRouteDefinition(model);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid route data: " + model, e);
        }
    }

    private RouteDefinition buildRouteDefinition(Route model) throws Exception {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(model.getId());
        routeDefinition.setUri(URI.create(model.getUri()));
        routeDefinition.setPredicates(objectMapper.readValue(model.getPredicates(), new TypeReference<>() {}));
        routeDefinition.setFilters(objectMapper.readValue(model.getFilters(), new TypeReference<>() {}));
        return routeDefinition;
    }
}
