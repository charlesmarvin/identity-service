package io.mmc.domain;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by charlesmarvin on 4/8/16.
 */
public class MongoIdentityService implements IdentityService {
    private final IdentityRepository repository;

    public MongoIdentityService(IdentityRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<Void> save(Identity identity) {
        return CompletableFuture.runAsync(() -> repository.save(identity));
    }

    @Override
    public CompletableFuture<Optional<Identity>> getIdentityByPrincipal(String principal) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(repository.findByPrincipal(principal)));
    }

    @Override
    public CompletableFuture<Collection<Identity>> findAll() {
        return CompletableFuture.supplyAsync(repository::findAll);
    }
}
