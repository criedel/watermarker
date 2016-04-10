package com.cupsoftware.test.services;

import com.cupsoftware.test.domain.Status;
import com.cupsoftware.test.domain.Ticket;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fake data source for {@link Ticket}s.
 */
@Component
public class TicketSource {

    private ConcurrentHashMap<String, Ticket> documentTicketId = new ConcurrentHashMap<>();

    /**
     * Finds a Ticket by a document’s id.
     * @param documentId
     * @return a copy of the ticket.
     */
    public Optional<Ticket> getTicketForDocument(final String documentId) {

        return Optional.ofNullable(documentTicketId.get(documentId)).map(Ticket::new);
    }

    /**
     * Updates the document’s ticket status and returns the Ticket.
     *
     * @param documentId
     * @param status
     * @return the new ticket
     */
    public Ticket updateTicket(final String documentId, final Status status) {

        final Ticket ticket = new Ticket(documentId, status);
        documentTicketId.put(documentId, ticket);

        return ticket;
    }
}
