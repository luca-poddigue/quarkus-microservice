package com.cuebiq.challenge.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Intercepts any exception related to bad request, such as invalid query params or body.
 * Logs the full stack trace to console and returns a more generic error message
 * providing a reason for the bad request.
 */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    private Logger logger = LoggerFactory.getLogger(BadRequestExceptionMapper.class);

    @Override
    public Response toResponse(BadRequestException exception) {
        logger.error("Bad request", exception);
        return Response.status(Response.Status.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .entity(exception.getMessage())
                .build();
    }
}