package io.mmc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.MongoClientURI;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

import java.net.UnknownHostException;

/**
 * Created by charlesmarvin on 4/8/16.
 */
public class ServiceConfiguration extends Configuration {
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;
    @JsonProperty("mongoConnectionUri")
    public String mongoConnectionUri;

    public MongoRepositoryFactory getMongoRepositoryFactory() throws UnknownHostException {
        MongoTemplate template = new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI(mongoConnectionUri)));
        return new MongoRepositoryFactory(template);
    }
}
