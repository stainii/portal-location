package be.stijnhooft.portal.location.cache;

import be.stijnhooft.portal.location.domain.GeocodeResult;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class CacheConfiguration {

    @Value("${be.stijnhooft.portal.location.cache.path:#{null}}")
    private String cachePath;

    @Value("${be.stijnhooft.portal.location.cache.geocode.max-mb:100}")
    private int geocodeCacheMaxMb;

    @Value("${be.stijnhooft.portal.location.cache.geocode.max-no-of-entries:1000}")
    private int geocodeCacheMaxNumberOfEntries;

    @Bean(name = "geocodeCache")
    public Cache<String, GeocodeResult> geocodeCache() {
        return buildPersistenceStorageCache("geocode", String.class, GeocodeResult.class, geocodeCacheMaxNumberOfEntries, geocodeCacheMaxMb);
    }


    @SuppressWarnings("rawtypes")
    private <K, V> Cache<K, V> buildPersistenceStorageCache(String cacheName, Class<K> keyType, Class<V> valueType, int maxNumberOfEntries, int maxMb) {
        CacheManagerBuilder cacheManagerCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder();

        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.heap(maxNumberOfEntries);
        if (cachePath != null) {
            String cachePath = Paths.get(this.cachePath, cacheName).toAbsolutePath().toString();
            cacheManagerCacheManagerBuilder = cacheManagerCacheManagerBuilder
                    .with(CacheManagerBuilder.persistence(cachePath));
            resourcePoolsBuilder = resourcePoolsBuilder
                    .disk(maxMb, MemoryUnit.MB, true);
        }

        CacheConfigurationBuilder<K, V> configurationBuilder =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(keyType, valueType, resourcePoolsBuilder)
                        .withExpiry(ExpiryPolicy.NO_EXPIRY);

        CacheManager cacheManager = cacheManagerCacheManagerBuilder
                .withCache(cacheName, configurationBuilder)
                .build();

        cacheManager.init();
        return cacheManager.getCache(cacheName, keyType, valueType);
    }

}
