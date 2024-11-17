package bg.fibank.cmsgateway.model;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class UserMetadata {
    private int userId;
    private List<String> userRoles;
}
