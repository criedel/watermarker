package com.cupsoftware.test.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Optional;

public class Journal extends Document {

    @JsonCreator
    public Journal(final @JsonProperty("id") String documentId,
                   final @JsonProperty("author") String author,
                   final @JsonProperty("title") String title,
                   final @JsonProperty("watermark") Optional<Watermark> watermark) {

        super(documentId, author, title, watermark);
    }

    @Override
    public Journal clone() {
        return new Journal(getDocumentId(), getAuthor(), getTitle(), getWatermark());
    }

    @Override
    public Journal with(final Watermark watermark) {
        return new Journal(getDocumentId(), getAuthor(), getTitle(), Optional.of(watermark));
    }

    @Override
    public Watermark createWatermark() {
        return new Watermark("journal", getTitle(), getAuthor(), Optional.empty());
    }

}
