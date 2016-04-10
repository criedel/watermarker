package com.cupsoftware.test.services;

import com.cupsoftware.test.domain.*;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dummy data source for {@link Document}s.
 */
@Component
public class DocumentSource {

    private final ConcurrentHashMap<String, Document> documentDB = new ConcurrentHashMap<>();

    public DocumentSource() {
        documentDB.put("1", new Book("1", Topic.SCIENCE, "Bruce Wayne", "The Dark Code", Optional.<Watermark>empty()));
        documentDB.put("2", new Book("2", Topic.BUSINESS, "Dr. Evil", "How to make money", Optional.<Watermark>empty()));
        documentDB.put("3", new Journal("3", "Clark Kent", "Journal of human flight routes", Optional.<Watermark>empty()));
        documentDB.put("4", new Book("4", Topic.MEDIA, "Mark Zuckerberg", "How to be friends with 1b people.", Optional.<Watermark>empty()));
    }

    /**
     * Looks up a document in our fake database.
     *
     * @param id document id
     * @return a copy of the requested document
     */
    public Optional<Document> getDocument(final String id) {
        return Optional.ofNullable(documentDB.get(id)).map(Document::clone);
    }

    /**
     * Stores a copy of the Document that includes the specified watermark.
     *
     * @param document that document of yours
     * @param watermark gets stored in the document
     * @return the saved document, just like in a real database!
     */
    public Document updateWatermark(final Document document, final Watermark watermark) {

        final Document updatedDocument = document.with(watermark);
        documentDB.put(document.getDocumentId(), updatedDocument);

        return updatedDocument;
    }
}
