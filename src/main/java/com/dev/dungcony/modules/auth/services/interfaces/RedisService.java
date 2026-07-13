package com.dev.dungcony.modules.auth.services.interfaces;

import java.util.Set;

public interface RedisService {

    // String operations
    void cache(String key, String value);

    void cache(String key, String value, long ttl);

    String getValue(String key);

    void delete(String key);

    void expire(String key, long ttl);

    // Set operations
    void addToSet(String key, String value);

    void removeFromSet(String key, String value);

    Set<String> getMembers(String key);
}