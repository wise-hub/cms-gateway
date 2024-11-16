package bg.fibank.cmsgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CmsGatewayApplication {
	public static void main(String[] args) {
		System.out.println("Java Version: " + System.getProperty("java.version"));
		System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
		System.out.println("Java Home: " + System.getProperty("java.home"));

		SpringApplication.run(CmsGatewayApplication.class, args);
	}
}
