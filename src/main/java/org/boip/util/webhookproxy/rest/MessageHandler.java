package org.boip.util.webhookproxy.rest;

import lombok.extern.slf4j.Slf4j;
import org.boip.util.webhookproxy.config.TargetConfig;
import org.boip.util.webhookproxy.rest.model.WebhookMessage;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class MessageHandler {
    private static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_NAME_TOKEN = "token";

    private final RestTemplateBuilder restTemplateBuilder;
    private final TargetConfig targetConfig;
    private HttpHeaders httpHeaders;

    public MessageHandler(RestTemplateBuilder restTemplateBuilder, TargetConfig targetConfig) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.targetConfig = targetConfig;
    }

    @PostConstruct
    private void initComponent() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(HEADER_NAME_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    public String relayMessage(WebhookMessage webhookMessage) {
        log.info("calling jenkins with token: {}", webhookMessage.getRepository().getName());
        httpHeaders.add(HEADER_NAME_TOKEN, webhookMessage.getRepository().getName());
        HttpEntity<WebhookMessage> httpEntity = new HttpEntity<>(webhookMessage, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplateBuilder
                .build()
                .exchange(targetConfig.getUrl(), HttpMethod.POST, httpEntity, String.class);
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            log.info(responseEntity.getBody());
        }
        return responseEntity.getBody();
    }
}
