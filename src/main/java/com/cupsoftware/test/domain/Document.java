package com.cupsoftware.test.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.alps.Doc;

import java.util.Optional;

/**
 * Mostly immutable, only the HATEOAS links (brought by {@link ResourceSupport} are mutable.
 */
public abstract class Document extends ResourceSupport implements Watermarkable, Cloneable {

    private final String documentId;

    private final String author;

    private final String title;

    private final Optional<Watermark> watermark;

    // TODO add properties for the actual content of the document, release date or other metadata.

    @JsonCreator
    public Document(final @JsonProperty("id") String documentId,
                    final @JsonProperty("author") String author,
                    final @JsonProperty("title") String title,
                    final @JsonProperty("watermark") Optional<Watermark> watermark) {
        this.documentId = documentId;
        this.author = author;
        this.title = title;
        this.watermark = watermark;
    }

    @JsonProperty("id")
    public String getDocumentId() {
        return documentId;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public Optional<Watermark> getWatermark() {
        return watermark;
    }

    @Override
    public abstract Document clone();

    /**
     * Similar to #clone. Implementations create a new document (journal or book) based on the current instance
     * and replace its watermark property.
     *
     * @param watermark
     * @return a copy of this document with the specified watermark
     */
    public abstract Document with(final Watermark watermark);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Document document = (Document) o;
        return Objects.equal(documentId, document.documentId) &&
                Objects.equal(author, document.author) &&
                Objects.equal(title, document.title) &&
                Objects.equal(watermark, document.watermark);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), documentId, author, title, watermark);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("documentId", documentId)
                .add("author", author)
                .add("title", title)
                .add("watermark", watermark)
                .addValue(super.toString())
                .toString();
    }
}
