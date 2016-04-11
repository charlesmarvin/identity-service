package io.mmc.domain;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by charlesmarvin on 4/8/16.
 */
public interface IdentityService {
    CompletableFuture<Optional<Identity>> getIdentityByPrincipal(String principal);
    CompletableFuture<Void> save(Identity identity);
    CompletableFuture<Collection<Identity>> findAll();
}
