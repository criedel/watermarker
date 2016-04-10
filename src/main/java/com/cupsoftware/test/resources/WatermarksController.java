package com.cupsoftware.test.resources;

import com.cupsoftware.test.domain.Document;
import com.cupsoftware.test.domain.Ticket;
import com.cupsoftware.test.exception.ResourceNotFoundException;
import com.cupsoftware.test.services.DocumentSource;
import com.cupsoftware.test.services.TicketSource;
import com.cupsoftware.test.services.WatermarkGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.cupsoftware.test.domain.Status.DONE;
import static com.cupsoftware.test.domain.Status.IN_PROGRESS;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * A simple REST interface for retrieving documents, creating watermarks and checking the watermark status.
 *
 * See the Readme.md for a how-to.
 */
@RestController
@RequestMapping(path = "/", produces = APPLICATION_JSON_UTF8_VALUE)
public class WatermarksController {

    @Autowired
    private DocumentSource documentSource;

    @Autowired
    private TicketSource ticketSource;

    @Autowired
    private WatermarkGenerator watermarkGenerator;

    @RequestMapping("/documents/{id}")
    @ResponseBody
    public HttpEntity<Document> getDocument(@PathVariable("id") final String id) {

        final Optional<Document> document = documentSource.getDocument(id).map(doc -> {

            if (!doc.getWatermark().isPresent()) {
                doc.add(linkTo(methodOn(WatermarksController.class).createWatermarkTicket(id)).withRel("watermark_ticket"));
            }

            doc.add(linkTo(methodOn(WatermarksController.class).getDocument(id)).withSelfRel());

            return doc;
        });

        return new ResponseEntity<>(document.orElseThrow(() -> new ResourceNotFoundException(String.format("Can’t find document with id %s", id))), OK);
    }

    @RequestMapping(value = "/watermark_tickets/{id}", method = RequestMethod.POST)
    @ResponseBody
    public HttpEntity<Ticket> createWatermarkTicket(@PathVariable("id") final String id) {

        final Optional<Ticket> ticketForDoc = documentSource.getDocument(id).map(doc -> {

            final Optional<Ticket> existingTicket = ticketSource.getTicketForDocument(id);

            if (!doc.getWatermark().isPresent()) {

                final Ticket ticket = ticketWithLinks(ticketSource.updateTicket(id, IN_PROGRESS));
                watermarkGenerator.generateWatermark(doc);

                return ticket;
            }

            return ticketWithLinks(existingTicket.orElse(new Ticket(id, DONE)));
        });

        return new ResponseEntity<>(ticketForDoc.orElseThrow(() -> new ResourceNotFoundException(String.format("Can’t find document with id %s", id))), CREATED);
    }

    @RequestMapping("/watermark_tickets/{id}")
    public HttpEntity<Ticket> pollTicketStatus(@PathVariable("id") final String id) {

        final Optional<Ticket> ticketForDoc = ticketSource.getTicketForDocument(id).map(WatermarksController::ticketWithLinks);

        return new ResponseEntity<>(ticketForDoc.orElseThrow(() -> new ResourceNotFoundException(String.format("Can’t find ticket for document with id %s", id))), OK);
    }

    private static Ticket ticketWithLinks(final Ticket ticket) {

        final String id = ticket.getDocumentId();
        ticket.add(linkTo(methodOn(WatermarksController.class).pollTicketStatus(id)).withSelfRel());
        ticket.add(linkTo(methodOn(WatermarksController.class).getDocument(id)).withRel("document"));

        return ticket;
    }
}
