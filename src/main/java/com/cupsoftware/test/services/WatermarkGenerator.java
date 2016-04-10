package com.cupsoftware.test.services;

import com.cupsoftware.test.domain.Document;
import com.cupsoftware.test.domain.Status;
import com.cupsoftware.test.domain.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * Basic async generator to create watermarks for documents with dummy implementation.
 */
@Component
public class WatermarkGenerator {

    @Autowired
    private DocumentSource documentSource;

    @Autowired
    private TicketSource ticketSource;

    @Async
    public Future<Ticket> generateWatermark(final Document document) {

        try {

            /*
             * Fakes an expensive watermark generation for testing purposes.
             *
             * I guess an actual watermark-process could be spun-off into a separate microservice
             * because this approach doesn’t scale well. If the main requirement for this service is to respond fast
             * and lay off the hard work in a background process then that process should be a separately scalable app.
             */
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            // TODO use proper logging instead
            e.printStackTrace();
        }

        // update document database
        documentSource.updateWatermark(document, document.createWatermark());

        // update ticket database
        final Ticket ticket = new Ticket(document.getDocumentId(), Status.DONE);
        ticketSource.updateTicket(document.getDocumentId(), Status.DONE);

        return new AsyncResult<>(ticket);
    }
}
