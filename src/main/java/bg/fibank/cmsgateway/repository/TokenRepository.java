package bg.fibank.cmsgateway.repository;

import bg.fibank.cmsgateway.model.UserMetadata;
import oracle.jdbc.OracleStruct;
import oracle.sql.ARRAY;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class TokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public TokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByToken(String token) {
        String sql = "SELECT COUNT(*) FROM TOKENS3 WHERE token = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, token) > 0;
    }

    public UserMetadata getUserMetadata(String token) throws SQLException {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_META")
                .withFunctionName("GET_USER_METADATA")
                .declareParameters(
                        new SqlOutParameter("RETURN", Types.STRUCT, "TYP_METADATA")
                );

        Map<String, Object> result = jdbcCall.execute(Map.of("TOKEN", token));
        OracleStruct struct = (OracleStruct) result.get("RETURN");

        Object[] attributes = struct.getAttributes();
        int userId = ((Number) attributes[0]).intValue();

        ARRAY rolesArray = (ARRAY) attributes[1];
        String[] userRoles = (String[]) rolesArray.getArray();

        return new UserMetadata(userId, List.of(userRoles));
    }
}
