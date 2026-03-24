package com.kojo.stack.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.io.IOException;
import java.net.URI;

/**
 * Cache configuration using Ehcache 3 (JCache/JSR-107) as in-memory cache provider.
 * Replaces the previous Redis-backed caching with a lightweight heap-based cache.
 *
 * Cache definitions and TTL policies are managed in ehcache.xml.
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() throws IOException {
        CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        URI configUri = new ClassPathResource("ehcache.xml").getURI();
        javax.cache.CacheManager jCacheManager = cachingProvider.getCacheManager(configUri, getClass().getClassLoader());
        log.info("Ehcache in-memory cache manager initialized with config: {}", configUri);
        return new JCacheCacheManager(jCacheManager);
    }
}
