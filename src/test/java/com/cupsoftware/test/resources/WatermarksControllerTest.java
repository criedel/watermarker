package com.cupsoftware.test.resources;

import com.cupsoftware.test.Application;
import com.cupsoftware.test.domain.Book;
import com.cupsoftware.test.domain.Status;
import com.cupsoftware.test.domain.Ticket;
import com.cupsoftware.test.domain.Watermark;
import com.cupsoftware.test.services.DocumentSource;
import com.cupsoftware.test.services.TicketSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class WatermarksControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private WatermarksController watermarksController;

    @Autowired
    private DocumentSource documentSource;

    @Autowired
    private TicketSource ticketSource;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(watermarksController).build();
    }

    @Test
    public void get_document() throws Exception {
        final MvcResult result = this.mockMvc.perform(get("/documents/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        final Book book1 = objectMapper.readValue(result.getResponse().getContentAsString(), Book.class);
        final Book bookFromDB = (Book) documentSource.getDocument("1").get();

        assertThat(book1.getTopic(), is(bookFromDB.getTopic()));
        assertThat(book1.getAuthor(), is(bookFromDB.getAuthor()));
        assertThat(book1.getDocumentId(), is(bookFromDB.getDocumentId()));
        assertThat(book1.getTitle(), is(bookFromDB.getTitle()));
        assertThat(book1.getWatermark(), is(bookFromDB.getWatermark()));

        assertThat(book1.getLink("self").getHref(), endsWith("/documents/1"));
        assertThat(book1.getLink("watermark_ticket").getHref(), endsWith("/watermark_tickets/1"));
    }

    @Test
    public void create_watermark_ticket() throws Exception {
        final MvcResult result = this.mockMvc.perform(post("/watermark_tickets/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        final Ticket ticket = objectMapper.readValue(result.getResponse().getContentAsString(), Ticket.class);
        final Ticket ticketFromDB = ticketSource.getTicketForDocument("1").get();

        assertThat(ticket.getDocumentId(), is(ticketFromDB.getDocumentId()));
        assertThat(ticket.getStatus(), is(Status.IN_PROGRESS));

        assertThat(ticket.getLink("self").getHref(), endsWith("/watermark_tickets/1"));
        assertThat(ticket.getLink("document").getHref(), endsWith("/documents/1"));
    }

    @Test
    public void get_document_create_watermark_and_poll() throws Exception {

        final MvcResult bookResult = this.mockMvc.perform(get("/documents/4").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        final Book book4 = objectMapper.readValue(bookResult.getResponse().getContentAsString(), Book.class);
        // we start with an empty watermark property
        assertThat(book4.getWatermark(), is(Optional.empty()));

        // create a watermark ticket
        final MvcResult createdTicket = this.mockMvc.perform(post(book4.getLink("watermark_ticket").getHref()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        final Ticket ticket = objectMapper.readValue(createdTicket.getResponse().getContentAsString(), Ticket.class);

        // poll the endpoint every 500ms and basically wait for the ticket to be processed
        int waitedForMillis = 0;
        while(pollTicket(ticket) == Status.IN_PROGRESS) {
            log.info("waiting...");
            Thread.sleep(500);
            waitedForMillis += 500;

            // enough is enough; if this thing waits for 6 seconds something is broken!
            assertThat(waitedForMillis, lessThan(6000));
        }

        log.info("Waited for {}ms", waitedForMillis);

        // now we can check the document’s watermark

        final MvcResult watermarkedBookResult = this.mockMvc.perform(get("/documents/4").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        final Book watermarkedBook = objectMapper.readValue(watermarkedBookResult.getResponse().getContentAsString(), Book.class);

        final Optional<Watermark> watermark = watermarkedBook.getWatermark();
        assertThat(watermark, notNullValue());
        assertThat(watermark, is(Optional.of(new Watermark("book", watermarkedBook.getTitle(), watermarkedBook.getAuthor(), Optional.of(watermarkedBook.getTopic())))));
    }

    /**
     *
     *
     * @param ticket
     * @return
     * @throws Exception
     */
    private Status pollTicket(final Ticket ticket) throws Exception {

        final MvcResult createdTicket = this.mockMvc.perform(get(ticket.getLink("self").getHref()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        final Ticket updatedTicket = objectMapper.readValue(createdTicket.getResponse().getContentAsString(), Ticket.class);

        return updatedTicket.getStatus();
    }
}