package pl.agh.order.management.rest.url;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.agh.order.management.rest.MicroService;

@Component
@Profile("prod")
public class KubernetesURLProvider implements URLProvider {

    @Override
    public String getBaseURL(MicroService microService) {
        return System.getenv(microService.getEnvVariableName());
    }
}
