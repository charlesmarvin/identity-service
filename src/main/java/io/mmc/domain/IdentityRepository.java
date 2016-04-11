package io.mmc.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by charlesmarvin on 4/8/16.
 */
public interface IdentityRepository extends MongoRepository<Identity, String> {
    Identity findByPrincipal(String principal);
}
