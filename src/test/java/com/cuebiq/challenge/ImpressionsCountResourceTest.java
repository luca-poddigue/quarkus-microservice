package com.cuebiq.challenge;

import com.cuebiq.challenge.model.TimeSpan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ImpressionsCountResourceTest {

    @InjectMocks
    private ImpressionsCountResource endpoint;

    @Mock
    private ImpressionsCountService service;

    @Test
    void whenGettingImpressionsCountByDevice_shouldCallTheRelatedService() throws IOException {
        endpoint.getByDevice();
        verify(service).getImpressionsCountByDevice();
    }

    @Test
    void whenGettingImpressionsCountByUsState_shouldCallTheRelatedService() throws IOException {
        endpoint.getByUsState();
        verify(service).getImpressionsCountByUsState();
    }

    @Test
    void givenAValidTimeSpanId_whenGettingImpressionsCountByTimeSpan_shouldCallTheRelatedService() throws IOException {
        endpoint.getByTimespan("HOD");
        verify(service).getImpressionsCountByTimeSpan(eq(TimeSpan.HOUR_OF_DAY));
    }

    @Test
    void givenAnInvalidTimeSpanId_whenGettingImpressionsCountByTimeSpan_shouldThrowAnException() {
        assertThrows(BadRequestException.class, () -> endpoint.getByTimespan("invalidTimespanId"));
    }
}