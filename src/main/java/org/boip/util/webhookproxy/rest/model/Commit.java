package org.boip.util.webhookproxy.rest.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Commit {
    private String id;
    private String message;
    private Date timestamp;
    private String url;
    private ArrayList<String> added;
    private ArrayList<String> removed;
    private ArrayList<String> modified;
}
