package bg.fibank.cmsgateway.repository;

import bg.fibank.cmsgateway.model.Route;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RouteRepository {

    private final JdbcTemplate jdbcTemplate;

    public RouteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Route> findAllRoutes() {
        String sql = "SELECT id, uri, predicates, filters FROM gateway_routes";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Route model = new Route();
            model.setId(rs.getString("id"));
            model.setUri(rs.getString("uri"));
            model.setPredicates(rs.getString("predicates"));
            model.setFilters(rs.getString("filters"));
            return model;
        });
    }
}
