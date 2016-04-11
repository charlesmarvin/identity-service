package io.mmc;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.mmc.domain.IdentityRepository;
import io.mmc.domain.IdentityResource;
import io.mmc.domain.IdentityService;
import io.mmc.domain.MongoIdentityService;
import io.mmc.domain.RedisIdentityService;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by charlesmarvin on 4/8/16.
 */
public class Service extends Application<ServiceConfiguration> {
    private static final String APP_NAME = "Identity Service";

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            String configFile = String.format("./config/%s.yml", System.getenv("ENV").toLowerCase());
            args = new String[] { "server", ClassLoader.getSystemResource(configFile).getFile() };
        }
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
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(ServiceConfiguration serviceConfiguration, Environment environment) throws Exception {
        ServiceConfiguration.RedisConfig redisConfig = serviceConfiguration.redisConfig;
        JedisPool pool = new JedisPool(new JedisPoolConfig(), redisConfig.host, redisConfig.port, redisConfig.timeout, redisConfig.password);
        RedisIdentityService redisIdentityService = new RedisIdentityService(pool);

        MongoRepositoryFactory factory = serviceConfiguration.getMongoRepositoryFactory();
        IdentityRepository repository = factory.getRepository(IdentityRepository.class);
        MongoIdentityService mongoIdentityService = new MongoIdentityService(repository);

        Map<String, IdentityService> map = new HashMap<>();
        map.put("mongo", mongoIdentityService);
        map.put("redis", redisIdentityService);
        final IdentityResource resource = new IdentityResource(map);

        environment.jersey().register(resource);
    }
}
