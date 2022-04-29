package org.boip.util.webhookproxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("target")
public class TargetConfig {
    private String url;
    private String token;
}
