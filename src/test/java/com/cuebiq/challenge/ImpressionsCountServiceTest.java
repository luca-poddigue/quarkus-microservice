package com.cuebiq.challenge;

import com.cuebiq.challenge.model.ImpressionBean;
import com.cuebiq.challenge.model.StatePolygonBean;
import com.cuebiq.challenge.model.TimeSpan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpressionsCountServiceTest {

    private static final int HOUR_1 = 10;
    private static final int HOUR_2 = 11;
    private static final BigDecimal DEVICE_ID_1 = new BigDecimal(1);
    private static final BigDecimal DEVICE_ID_2 = new BigDecimal(2);
    private static final String STATE = "state";

    private static List<ImpressionBean> impressions;
    private static List<StatePolygonBean> statePolygons;

    @Mock
    private ImpressionsCountRepository impressionsCountRepository;
    @InjectMocks
    private ImpressionsCountService impressionsCountService;

    @BeforeAll
    static void beforeAll() {
        impressions = Arrays.asList(
                new ImpressionBean(DEVICE_ID_1, new BigDecimal(34.5), new BigDecimal(12.5), LocalDateTime.of(2019, 3, 15, HOUR_1, 20)),
                new ImpressionBean(DEVICE_ID_2, new BigDecimal(35.5), new BigDecimal(13.5), LocalDateTime.of(2018, 3, 16, HOUR_1, 25)),
                new ImpressionBean(DEVICE_ID_1, new BigDecimal(40), new BigDecimal(12.5), LocalDateTime.of(2018, 3, 16, HOUR_2, 25))
        );

        statePolygons = Collections.singletonList(
                new StatePolygonBean(
                        STATE,
                        buildSquarePolygon(34, 12, 4),
                        Collections.singletonList(buildSquarePolygon(35, 13, 1))));
    }

    private static Path2D buildSquarePolygon(double x, double y, int size) {
        Path2D polygon = new Path2D.Double();
        polygon.moveTo(x, y);
        polygon.lineTo(x, y + size);
        polygon.lineTo(x + size, y + size);
        polygon.lineTo(x + size, y);
        polygon.lineTo(x, y);
        return polygon;
    }

    @BeforeEach
    void setUp() throws IOException {
        when(impressionsCountRepository.loadImpressions()).thenReturn(impressions.stream());
    }

    @Test
    void givenTheListOfImpressions_whenCountingImpressionsByTimeSpan_shouldReturnAMapWithCorrectGroupsAndCounts() throws IOException {

        Map<Integer, Long> map = impressionsCountService.getImpressionsCountByTimeSpan(TimeSpan.HOUR_OF_DAY);

        assertThat(map.size(), is(equalTo(2)));
        assertThat(map.keySet(), containsInAnyOrder(equalTo(HOUR_1), equalTo(HOUR_2)));
        assertThat(map.get(HOUR_1), is(equalTo(2L)));
        assertThat(map.get(HOUR_2), is(equalTo(1L)));
    }

    @Test
    void givenTheListOfImpressions_whenCountingImpressionsByDevice_shouldReturnAMapWithCorrectGroupsAndCounts() throws IOException {
        Map<BigDecimal, Long> map = impressionsCountService.getImpressionsCountByDevice();

        assertThat(map.size(), is(equalTo(2)));
        assertThat(map.keySet(), containsInAnyOrder(equalTo(DEVICE_ID_1), equalTo(DEVICE_ID_2)));
        assertThat(map.get(DEVICE_ID_1), is(equalTo(2L)));
        assertThat(map.get(DEVICE_ID_2), is(equalTo(1L)));
    }

    @Test
    void givenTheListOfImpressions_whenCountingImpressionsByUsState_shouldReturnAMapWithCorrectGroupsAndCounts() throws IOException {
        when(impressionsCountRepository.loadStatePolygons()).thenReturn(statePolygons);

        Map<String, Long> map = impressionsCountService.getImpressionsCountByUsState();

        assertThat(map.size(), is(equalTo(1)));
        assertThat(map.keySet(), contains(equalTo(STATE)));
        assertThat(map.get(STATE), is(equalTo(1L)));
    }
}