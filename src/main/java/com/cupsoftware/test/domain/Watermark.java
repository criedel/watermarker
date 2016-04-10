package com.cupsoftware.test.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Optional;

/**
 * An immutable watermark that can be used for journals or books.
 */
public class Watermark {

    private final String content;

    private final String title;

    private final String author;

    private final Optional<Topic> topic;

    @JsonCreator
    public Watermark(@JsonProperty("content") final String content,
                     @JsonProperty("title") final String title,
                     @JsonProperty("author") final String author,
                     @JsonProperty("topic") final Optional<Topic> topic) {
        this.content = content;
        this.title = title;
        this.author = author;
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Optional<Topic> getTopic() {
        return topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Watermark watermark = (Watermark) o;
        return Objects.equal(content, watermark.content) &&
                Objects.equal(title, watermark.title) &&
                Objects.equal(author, watermark.author) &&
                Objects.equal(topic, watermark.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(content, title, author, topic);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("content", content)
                .add("title", title)
                .add("author", author)
                .add("topic", topic)
                .toString();
    }
}

