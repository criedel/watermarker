## Watermarker

A Java / Spring-Boot application.

## How to use

Run using maven:

    mvn spring-boot:run

Get any document, for example:

    curl -XGET http://localhost:8080/documents/1

Response:

    {
    	"id": "1",
    	"topic": "Science",
    	"author": "Bruce Wayne",
    	"title": "The Dark Code",
    	"links": [{
    		"rel": "watermark_ticket",
    		"href": "http://localhost:8080/watermark_tickets/1"
    	}, {
    		"rel": "self",
    		"href": "http://localhost:8080/documents/1"
    	}]
    }
 
Follow the link "create_watermark" to create a new watermark:

    curl -XPOST http://localhost:8080/watermark_tickets/1

Response:

    {
    	"documentId": "1",
    	"status": "IN_PROGRESS",
    	"links": [{
    		"rel": "self",
    		"href": "http://localhost:8080/watermark_tickets/1"
    	}, {
    		"rel": "document",
    		"href": "http://localhost:8080/documents/1"
    	}]
    }
    
 Poll the link "self" a few times. After a few seconds the "status" should change from "IN_PROGRESS" to "DONE":

    {
    	"documentId": "1",
    	"status": "DONE",
    	"links": [{
    		"rel": "self",
    		"href": "http://localhost:8080/watermark_tickets/1"
    	}, {
    		"rel": "document",
    		"href": "http://localhost:8080/documents/1"
    	}]
    }

Now get back to the document using the "document" link and the "watermark" property should be present (and the watermark ticket link has been removed):

    {
    	"id": "1",
    	"topic": "Science",
    	"author": "Bruce Wayne",
    	"title": "The Dark Code",
    	"watermark": {
    		"content": "book",
    		"title": "The Dark Code",
    		"author": "Bruce Wayne",
    		"topic": "Science"
    	},
    	"links": [{
    		"rel": "self",
    		"href": "http://localhost:8080/documents/1"
    	}]
    }
    
All pre-defined documents:

    curl -XGET http://localhost:8080/documents/1
    curl -XGET http://localhost:8080/documents/2
    curl -XGET http://localhost:8080/documents/3
    curl -XGET http://localhost:8080/documents/4

Restart the app to reset the dummy-database.