package com.ritesh.product_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cache")
public class ProductCacheProperties {

    private Duration productTtl;
}