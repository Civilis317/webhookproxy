package org.boip.util.webhookproxy.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.boip.util.webhookproxy.rest.model.WebhookMessage;
import org.boip.util.webhookproxy.validation.ValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1")
public class ProxyController extends AbstractController {

    @Value("${app.skip_validation}")
    private boolean skipValidation;

    private final ValidationService validationService;
    private final MessageHandler messageHandler;

    public ProxyController(ValidationService validationService, MessageHandler messageHandler) {
        this.validationService = validationService;
        this.messageHandler = messageHandler;
    }

    @PostMapping(value = "/receive", produces = MediaType.APPLICATION_JSON_VALUE)
    public String receiveWebHook(HttpServletRequest request) throws IOException {
        String payload = request.getReader().lines().collect(Collectors.joining("\n"));

        if (skipValidation) {
            log.info("Signature validation skipped");
            log.info("This should only be implemented while developing");
        } else {
            validationService.validateSignature(request.getHeader("X-Hub-Signature-256"), payload, request.getCharacterEncoding());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WebhookMessage webhookMessage = objectMapper.readValue(payload, WebhookMessage.class);
        return messageHandler.relayMessage(webhookMessage);
    }
}
