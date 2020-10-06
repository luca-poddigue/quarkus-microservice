package com.cuebiq.challenge.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Intercepts any exception that was not caught as part of the business logic or by other exception mappers.
 * Logs the full stack trace to console and returns a generic internal server error message to the client.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    private Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        logger.error("Internal server error", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .entity("Cannot fulfill the request due to an internal error.")
                .build();
    }
}