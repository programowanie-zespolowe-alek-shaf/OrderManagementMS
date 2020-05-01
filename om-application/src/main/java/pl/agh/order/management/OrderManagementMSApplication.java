package pl.agh.order.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("pl.agh.order.management")
@EnableJpaRepositories("pl.agh.order.management")
@EnableEurekaClient
public class OrderManagementMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderManagementMSApplication.class, args);
    }
}
