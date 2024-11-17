package bg.fibank.cmsgateway.repository;

import bg.fibank.cmsgateway.model.UserMetadata;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.util.Arrays;

@Repository
public class TokenRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final String STORED_PROCEDURE_CALL = "{? = call PKG_META.GET_USER_METADATA(?)}";

    public TokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserMetadata getUserMetadata(String token) {
        return jdbcTemplate.execute((ConnectionCallback<UserMetadata>) connection -> {
            try (CallableStatement cs = connection.prepareCall(STORED_PROCEDURE_CALL)) {
                cs.registerOutParameter(1, OracleTypes.STRUCT, "TYP_METADATA");
                cs.setString(2, token);
                cs.execute();

                var attributes = ((STRUCT) cs.getObject(1)).getAttributes();
                return new UserMetadata(
                        ((Number) attributes[0]).intValue(),
                        Arrays.asList((String[]) ((ARRAY) attributes[1]).getArray())
                );
            } catch (Exception e) {
                throw new RuntimeException("Database operation failed for token: " + token, e);
            }
        });
    }
}
