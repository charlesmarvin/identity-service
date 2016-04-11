package io.mmc.domain;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
                Set<String> keySet = jedis.keys("*");
                String[] keyArray = new String[keySet.size()];
                jedis.keys("*").toArray(keyArray);
                List<String> values = jedis.mget(keyArray);
                List<Identity> result = new ArrayList<>(keyArray.length);
                for (int i = 0; i < keyArray.length; i++) {
                    result.add(new Identity(keyArray[i], Identity.State.valueOf(values.get(i))));
                }
                return result;
            }
        });
    }
}
