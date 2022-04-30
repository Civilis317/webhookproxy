package org.boip.util.webhookproxy.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Repository {
    private int id;
    private String name;

    @JsonProperty("full_name")
    private String fullNname;
    private String description;
    private String url;

    @JsonProperty("updated_at")
    private Date modified;
    private String language;
}
