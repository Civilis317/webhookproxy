package org.boip.util.webhookproxy.rest;

import lombok.extern.slf4j.Slf4j;
import org.boip.util.webhookproxy.rest.model.WebhookMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1")
public class ProxyController {

    private final MessageHandler messageHandler;

    public ProxyController(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @PostMapping(value = "/receive", produces = MediaType.APPLICATION_JSON_VALUE)
    public String receiveWebHook(@RequestBody WebhookMessage message) {
        log.debug("incoming request: {}", message.getRef());
        log.debug("commit message: {}", message.getHead_commit().getMessage());
        return messageHandler.relayMessage(message);
    }

}
