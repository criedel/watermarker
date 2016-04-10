package com.cupsoftware.test.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.springframework.hateoas.ResourceSupport;

public class Ticket extends ResourceSupport {

    private final String documentId;

    private final Status status;

    @JsonCreator
    public Ticket(@JsonProperty("documentId") final String documentId,
                  @JsonProperty("status") final Status status) {
        this.documentId = documentId;
        this.status = status;
    }

    public Ticket(final Ticket ticket) {
        this.documentId = ticket.getDocumentId();
        this.status = ticket.getStatus();
    }

    public String getDocumentId() {
        return documentId;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).toString();
    }
}
