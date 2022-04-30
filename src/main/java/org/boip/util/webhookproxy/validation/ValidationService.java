package org.boip.util.webhookproxy.validation;

import lombok.extern.slf4j.Slf4j;
import org.boip.util.webhookproxy.exception.DecoderException;
import org.boip.util.webhookproxy.exception.ValidationException;
import org.boip.util.webhookproxy.rest.model.WebhookMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ValidationService {
    private static final String SIGNATURE_PREFIX = "sha256=";
    private static final String HMAC_SHA_ALGORITHM = "HmacSHA256";

    @Value("${github.secret}")
    private String secretToken;

    public void validateSignature(HttpServletRequest request) throws IOException {

        String payload = request.getReader().lines().collect(Collectors.joining("\n"));
        try {
            if (!validateSignature256(request.getHeader("X-Hub-Signature-256"), payload, request.getCharacterEncoding())) {
                throw new ValidationException("signature validation failed");
            }
        } catch (UnsupportedEncodingException e) {
            throw new ValidationException("unable to validate signature");

        }
    }

    private boolean validateSignature256(String signatureHeader, String payload, String encoding) throws UnsupportedEncodingException {

        if (secretToken == null || secretToken.equals("")) {
            log.error("webhookSecret not configured. Skip signature validation");
            return false;
        }

        if (!signatureHeader.startsWith(SIGNATURE_PREFIX)) {
            log.error("Unsupported webhook signature type: {}", signatureHeader);
            return false;
        }
        byte[] signatureHeaderBytes;
        try {
            signatureHeaderBytes = decodeHex(signatureHeader.substring(SIGNATURE_PREFIX.length()).toCharArray());
            log.info("header signature: " + bytesToHex(signatureHeaderBytes));
        } catch (DecoderException e) {
            log.error("Invalid signature: {}", signatureHeader);
            return false;
        }
        byte[] expectedSignature = getExpectedSignature(payload.getBytes(encoding == null ? "UTF-8" : encoding), secretToken);
        log.info("generated signature: " + bytesToHex(expectedSignature));
        return MessageDigest.isEqual(signatureHeaderBytes, expectedSignature);
    }


    private byte[] getExpectedSignature(byte[] payload, String secretToken) {
        SecretKeySpec key = new SecretKeySpec(secretToken.getBytes(), HMAC_SHA_ALGORITHM);
        Mac hmac;
        try {
            hmac = Mac.getInstance(HMAC_SHA_ALGORITHM);
            hmac.init(key);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hmac SHA must be supported", e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Hmac SHA must be compatible to Hmac SHA Secret Key", e);
        }
        return hmac.doFinal(payload);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static byte[] decodeHex(final char[] data) throws DecoderException {
        final byte[] out = new byte[data.length >> 1];
        final int outOffset = 0;
        final int len = data.length;

        if ((len & 0x01) != 0) {
            throw new DecoderException("Odd number of characters.");
        }

        final int outLen = len >> 1;
        if (out.length - outOffset < outLen) {
            throw new DecoderException("Output array is not large enough to accommodate decoded data.");
        }

        // two characters form the hex value.
        for (int i = outOffset, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }


    protected static int toDigit(final char ch, final int index) throws DecoderException {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }


}
