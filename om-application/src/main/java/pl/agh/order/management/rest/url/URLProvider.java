package pl.agh.order.management.rest.url;


import pl.agh.order.management.rest.MicroService;

public interface URLProvider {

    String getBaseURL(MicroService microService);
}
