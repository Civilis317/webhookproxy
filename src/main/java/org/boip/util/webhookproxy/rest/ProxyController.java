package org.boip.util.webhookproxy.rest;

import lombok.extern.slf4j.Slf4j;
import org.boip.util.webhookproxy.exception.ValidationException;
import org.boip.util.webhookproxy.rest.model.WebhookMessage;
import org.boip.util.webhookproxy.validation.ValidationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.boip.util.webhookproxy.util.StringUtils.isEmpty;

@Slf4j
@RestController
@RequestMapping("/v1")
public class ProxyController extends AbstractController {

    private final ValidationService validationService;
    private final MessageHandler messageHandler;

    public ProxyController(ValidationService validationService, MessageHandler messageHandler) {
        this.validationService = validationService;
        this.messageHandler = messageHandler;
    }

    @PostMapping(value = "/receive", produces = MediaType.APPLICATION_JSON_VALUE)
    public void receiveWebHook(HttpServletRequest request) throws IOException {
        validationService.validateSignature(request);
        log.info("signature validation ok");
    }

//
//    @PostMapping(value = "/receivex", produces = MediaType.APPLICATION_JSON_VALUE)
//    public String receiveWebHook(@RequestBody WebhookMessage message, HttpServletRequest request) {
//        validationService.validateSignature(request.getHeader("X-Hub-Signature-256"), message, request.getCharacterEncoding());
//        log.info("incoming request: repo: {}, branch:{}", message.getRepository().getName(), message.getRef());
//        log.info("commit message: {}", message.getHead_commit().getMessage());
//
//        return messageHandler.relayMessage(message);
//    }

    private void validate(WebhookMessage message, HttpServletRequest request) {


        ValidationException ve = new ValidationException();

        String sourceIp = request.getHeader("X-FORWARDED-FOR");
        if (sourceIp == null) sourceIp = request.getRemoteAddr();

        String sha1Signature = request.getHeader("X-Hub-Signature");
        String sha256Signature = request.getHeader("X-Hub-Signature-256");

        if (isEmpty(sha1Signature))
            ve.addMessage("missing github header: X-Hub-Signature");

        if (isEmpty(sha256Signature))
            ve.addMessage("missing github header: X-Hub-Signature-256");

        log.info("X-Hub-Signature: {}", sha1Signature);
        log.info("X-Hub-Signature-256: {}", sha256Signature);
        log.info("User-Agent: {}", request.getHeader("User-Agent"));
        log.info("X-GitHub-Delivery: {}", request.getHeader("X-GitHub-Delivery"));
        log.info("source ip address: {}", sourceIp);

        if (! ve.validationOK())
            throw ve;
    }

}
