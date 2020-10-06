package com.cuebiq.challenge.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GenericExceptionMapperTest {

    private static final IOException EXPECTED_EXCEPTION = new IOException();

    @InjectMocks
    private GenericExceptionMapper mapper;

    @Mock
    private Logger logger;

    private Response response;

    @BeforeEach
    void setUp() {
        response = mapper.toResponse(EXPECTED_EXCEPTION);
    }

    @Test
    void givenAnException_whenInterceptingException_shouldReturnAnInternalServerError() {
        assertThat(response.getStatus(), is(equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())));
    }

    @Test
    void givenAnException_whenInterceptingException_shouldRespondWithContentTypeText() {
        assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(equalTo(MediaType.TEXT_PLAIN)));
    }

    @Test
    void givenAnException_whenInterceptingException_shouldLogTheExeption() {
        verify(logger).error(anyString(), eq(EXPECTED_EXCEPTION));
    }


}