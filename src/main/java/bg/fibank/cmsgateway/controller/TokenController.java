package bg.fibank.cmsgateway.controller;

import bg.fibank.cmsgateway.repository.TokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenRepository tokenRepository;

    public TokenController(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @GetMapping("/test-token")
    public ResponseEntity<Boolean> testToken(@RequestParam String token) {
        boolean exists = tokenRepository.existsByToken(token);
        return ResponseEntity.ok(exists);
    }
}
