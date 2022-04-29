package org.boip.util.webhookproxy.rest.model;

import lombok.Data;
import org.boip.util.webhookproxy.rest.model.Actor;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Commit {
    private String id;
    private String tree_id;
    private boolean distinct;
    private String message;
    private Date timestamp;
    private String url;
    private Actor author;
    private Actor committer;
    private ArrayList<String> added;
    private ArrayList<String> removed;
    private ArrayList<String> modified;
}
