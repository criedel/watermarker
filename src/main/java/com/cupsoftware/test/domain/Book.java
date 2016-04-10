package com.cupsoftware.test.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;


import java.util.Optional;

public class Book extends Journal {

    private final Topic topic;

    @JsonCreator
    public Book(final @JsonProperty("id") String documentId,
                final @JsonProperty("topic") Topic topic,
                final @JsonProperty("author") String author,
                final @JsonProperty("title") String title,
                final @JsonProperty("watermark") Optional<Watermark> watermark) {
        super(documentId, author, title, watermark);
        this.topic = topic;
    }

    @Override
    public Book clone() {
        return new Book(getDocumentId(), getTopic(), getAuthor(), getTitle(), getWatermark());
    }

    @Override
    public Book with(final Watermark watermark) {
        return new Book(getDocumentId(), getTopic(), getAuthor(), getTitle(), Optional.of(watermark));
    }

    @Override
    public Watermark createWatermark() {
        return new Watermark("book", getTitle(), getAuthor(), Optional.of(getTopic()));
    }

    public Topic getTopic() {
        return topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Book book = (Book) o;
        return topic == book.topic;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), topic);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("topic", topic).addValue(super.toString()).toString();
    }
}
