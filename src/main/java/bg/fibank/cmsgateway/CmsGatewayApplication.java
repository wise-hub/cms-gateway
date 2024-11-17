package bg.fibank.cmsgateway;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class CmsGatewayApplication {
	public static void main(String[] args) {
		System.out.println("Java Version: " + System.getProperty("java.version"));
		System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
		System.out.println("Java Home: " + System.getProperty("java.home"));

		SpringApplication.run(CmsGatewayApplication.class, args);
	}

	@PostConstruct
	public void configureDnsCache() {
		Security.setProperty("networkaddress.cache.ttl", "3600");
		Security.setProperty("networkaddress.cache.negative.ttl", "10");
	}
}
