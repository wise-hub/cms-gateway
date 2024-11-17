package bg.fibank.cmsgateway.service;

import bg.fibank.cmsgateway.model.Route;
import bg.fibank.cmsgateway.repository.RouteRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RouteService implements RouteDefinitionLocator {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.defer(() -> {
            List<Route> routes = routeRepository.findAllRoutes();
            List<RouteDefinition> routeDefinitions = new ArrayList<>();
            for (Route route : routes) {
                RouteDefinition routeDefinition = new RouteDefinition();
                routeDefinition.setId(route.getId());
                routeDefinition.setUri(URI.create(route.getUri()));
                routeDefinition.setPredicates(List.of(new PredicateDefinition("Path=" + route.getPredicates())));
                routeDefinition.setFilters(parseFilters(route.getFilters()));
                routeDefinitions.add(routeDefinition);
            }
            return Flux.fromIterable(routeDefinitions);
        });
    }

    public List<Route> getLoadedRoutesAsObjects() {
        return routeRepository.findAllRoutes();
    }

    private List<FilterDefinition> parseFilters(String filters) {
        List<FilterDefinition> filterDefinitions = new ArrayList<>();
        if (filters == null || filters.isEmpty()) return filterDefinitions;

        for (String filter : filters.split(";")) {
            String[] parts = filter.split(":", 2);
            FilterDefinition filterDefinition = new FilterDefinition();
            filterDefinition.setName(parts[0]);
            if (parts.length > 1) {
                String[] args = parts[1].split("->", 2);
                if (args.length == 1) {
                    filterDefinition.addArg("value", args[0]);
                } else {
                    filterDefinition.addArg("regexp", args[0]);
                    filterDefinition.addArg("replacement", args[1]);
                }
            }
            filterDefinitions.add(filterDefinition);
        }
        return filterDefinitions;
    }
}
