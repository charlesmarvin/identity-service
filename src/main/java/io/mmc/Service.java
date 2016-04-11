package io.mmc;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.mmc.domain.IdentityRepository;
import io.mmc.domain.IdentityResource;
import io.mmc.domain.MongoIdentityService;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

/**
 * Created by charlesmarvin on 4/8/16.
 */
public class Service extends Application<ServiceConfiguration> {
    private static final String APP_NAME = "Identity Service";

    public static void main(String[] args) throws Exception {
        new Service().run(args);
    }

    @Override
    public String getName() {
        return APP_NAME;
    }

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<ServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ServiceConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(ServiceConfiguration serviceConfiguration, Environment environment) throws Exception {
        MongoRepositoryFactory factory = serviceConfiguration.getMongoRepositoryFactory();
        IdentityRepository repository = factory.getRepository(IdentityRepository.class);
        final IdentityResource resource = new IdentityResource(new MongoIdentityService(repository));
        environment.jersey().register(resource);
    }
}
