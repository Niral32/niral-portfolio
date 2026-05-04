package com.niralpatel.portfolio.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, AdminProperties.class, PortfolioCorsProperties.class})
public class ConfigurationBeans {
}
