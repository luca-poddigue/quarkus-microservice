package com.cuebiq.challenge.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BadRequestExceptionMapperTest {

    private static final BadRequestException EXPECTED_EXCEPTION = new BadRequestException();

    @InjectMocks
    private BadRequestExceptionMapper mapper;

    @Mock
    private Logger logger;

    private Response response;

    @BeforeEach
    void setUp() {
        response = mapper.toResponse(EXPECTED_EXCEPTION);
    }

    @Test
    void givenAnException_whenInterceptingException_shouldReturnAnInternalServerError() {
        assertThat(response.getStatus(), is(equalTo(Response.Status.BAD_REQUEST.getStatusCode())));
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