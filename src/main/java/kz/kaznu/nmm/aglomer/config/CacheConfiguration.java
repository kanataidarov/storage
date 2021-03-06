package kz.kaznu.nmm.aglomer.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.*;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.hibernate.cache.jcache.ConfigSettings;

import java.util.concurrent.TimeUnit;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import io.github.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();
        Config config = new Config();
        config.useSingleServer().setAddress(jHipsterProperties.getCache().getRedis().getServer());
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration())));
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, kz.kaznu.nmm.aglomer.domain.PropertyGroup.class.getName(), jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.Property.class.getName(), jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.PropertyGroup.class.getName() + ".propertyGroupProperties", jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.RecordGroup.class.getName(), jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.RecordGroup.class.getName() + ".recordGroupRecordTemplates", jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.RecordTemplate.class.getName(), jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.RecordTemplate.class.getName() + ".recordTemplateRecordFields", jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.RecordField.class.getName(), jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.RecordField.class.getName() + ".recordFieldRecordValues", jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.Record.class.getName(), jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.Record.class.getName() + ".recordRecordValues", jcacheConfiguration);
            createCache(cm, kz.kaznu.nmm.aglomer.domain.RecordValue.class.getName(), jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName, javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

}
