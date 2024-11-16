package bg.fibank.cmsgateway.model;

import lombok.Data;

@Data
public class Route {
    private String id;
    private String uri;
    private String predicates;
    private String filters;
}
