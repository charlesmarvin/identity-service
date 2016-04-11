package io.mmc.domain;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by charlesmarvin on 4/10/16.
 */
public class RedisIdentityService implements IdentityService {

    private final JedisPool jedisPool;

    public RedisIdentityService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public CompletableFuture<Optional<Identity>> getIdentityByPrincipal(String principal) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                String state = jedis.get(principal);
                return (StringUtils.isNotBlank(state)) ? Optional.of(new Identity(principal, Identity.State.valueOf(state))) : Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Void> save(Identity identity) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set(identity.getPrincipal(), identity.getState().name());
            }
        });
    }

    @Override
    public CompletableFuture<Collection<Identity>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.keys("*").stream()
                        .map(key -> new Identity(key, Identity.State.valueOf(jedis.get(key))))
                        .collect(Collectors.toList());
            }
        });
    }
}
