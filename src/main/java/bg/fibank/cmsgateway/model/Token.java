package bg.fibank.cmsgateway.model;

import lombok.Data;

@Data
public class Token {
    private String token;
    private int userId;
}
