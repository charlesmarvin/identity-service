package io.mmc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by charlesmarvin on 4/9/16.
 */
public class MongoConfiguration {
    @JsonProperty
    @NotEmpty
    public String host;

    @JsonProperty
    @Min(1)
    @Max(65535)
    public int port;

    @JsonProperty
    @NotEmpty
    public String database;
}
