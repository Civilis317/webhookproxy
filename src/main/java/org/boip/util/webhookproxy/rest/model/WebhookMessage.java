package org.boip.util.webhookproxy.rest.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class WebhookMessage {
    private String ref;
    private String before;
    private String after;
    private Repository repository;
    private Actor pusher;
    private Actor sender;
    private boolean created;
    private boolean deleted;
    private boolean forced;
    private Object base_ref;
    private String compare;
    private ArrayList<Commit> commits;
    private Commit head_commit;
}
