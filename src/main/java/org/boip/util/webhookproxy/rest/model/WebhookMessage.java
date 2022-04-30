package org.boip.util.webhookproxy.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class WebhookMessage {
    @JsonProperty("ref")
    private String branch;
    private Repository repository;
    private ArrayList<Commit> commits;
    private Commit head_commit;
}
