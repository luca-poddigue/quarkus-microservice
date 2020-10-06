package com.cuebiq.challenge;

import com.cuebiq.challenge.model.ImpressionBean;
import com.cuebiq.challenge.model.StatePolygonBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ImpressionsCountRepositoryTest {

    @InjectMocks
    private ImpressionsCountRepository repository;

    @Mock
    private Logger logger;

    @Test
    void givenAPolygon_whenLoadingStatePolygons_shouldFillAListOfCorrectLength() {
        repository.statesMapFileName = "mapWithPolygon_test.json";

        List<StatePolygonBean> statePolygons = repository.loadStatePolygons();

        assertThat(statePolygons, hasSize(1));
    }

    @Test
    void givenAPolygon_whenLoadingStatePolygons_shouldSetTheCorrectStateName() {
        repository.statesMapFileName = "mapWithPolygon_test.json";

        StatePolygonBean statePolygon = repository.loadStatePolygons().get(0);

        assertThat(statePolygon.getName(), is(equalTo("Ohio")));
    }

    @Test
    void givenAPolygon_whenLoadingStatePolygons_shouldBuildThePolygon() {
        repository.statesMapFileName = "mapWithPolygon_test.json";

        StatePolygonBean statePolygon = repository.loadStatePolygons().get(0);
        double[] firstPointCoordinates = firstPointCoordinates(statePolygon.getPolygon());

        assertThat(polygonSize(statePolygon.getPolygon()), is(equalTo(169)));
        assertThat(firstPointCoordinates[0], is(closeTo(-78.86, 0.01)));
        assertThat(firstPointCoordinates[1], is(closeTo(42.83, 0.01)));
    }

    @Test
    void givenAPolygon_whenLoadingStatePolygons_shouldBuildExclusionPolygons() {
        repository.statesMapFileName = "mapWithPolygon_test.json";

        StatePolygonBean statePolygon = repository.loadStatePolygons().get(0);
        double[] firstPointCoordinates = firstPointCoordinates(statePolygon.getExclusionPolygons().get(0));

        assertThat(statePolygon.getExclusionPolygons(), hasSize(1));
        assertThat(polygonSize(statePolygon.getExclusionPolygons().get(0)), is(equalTo(5)));

        assertThat(polygonSize(statePolygon.getPolygon()), is(equalTo(169)));
        assertThat(firstPointCoordinates[0], is(closeTo(-83.16, 0.01)));
        assertThat(firstPointCoordinates[1], is(closeTo(42.09, 0.01)));
    }

    @Test
    void givenADatasetCsv_whenLoadingImpressions_shouldPrepareAStreamOfCorrectLength() throws IOException {
        repository.datasetFileName = "dataset_test.csv";

        List<ImpressionBean> impressions = repository.loadImpressions().collect(Collectors.toList());

        assertThat(impressions, hasSize(3));
    }

    @Test
    void givenADatasetCsv_whenLoadingImpressions_shouldFillAListOfImpressionBeans() throws IOException {
        repository.datasetFileName = "dataset_test.csv";

        ImpressionBean impression = repository.loadImpressions().collect(Collectors.toList()).get(0);

        assertThat(impression.getDeviceId(), is(equalTo(new BigDecimal("6581"))));
        assertThat(impression.getTimestamp(), is(equalTo(LocalDateTime.of(2012, 12, 12, 15, 47, 52, 76000000))));
        assertThat(impression.getLatitude(), is(equalTo(new BigDecimal("79.78258825713505"))));
        assertThat(impression.getLongitude(), is(equalTo(new BigDecimal("153.3239674172064"))));
    }

    @Test
    void givenAMultiPolygon_whenLoadingStatePolygons_shouldFillAListOfCorrectLength() {
        repository.statesMapFileName = "mapWithMultiPolygon_test.json";

        List<StatePolygonBean> statePolygons = repository.loadStatePolygons();

        assertThat(statePolygons, hasSize(2));
    }

    @Test
    void givenAMultiPolygon_whenLoadingStatePolygons_shouldFillAListOfStatePolygons() {
        repository.statesMapFileName = "mapWithMultiPolygon_test.json";

        StatePolygonBean statePolygon = repository.loadStatePolygons().get(0);

        assertThat(statePolygon.getName(), is(equalTo("Alabama")));
        assertThat(polygonSize(statePolygon.getPolygon()), is(equalTo(4)));
        assertThat(statePolygon.getExclusionPolygons(), is(empty()));
    }

    @Test
    void givenAnUnexpectedGeometryType_whenLoadingStatePolygons_shouldLogAWarning() {
        repository.statesMapFileName = "mapWithUnexpectedGeometryType_test.json";

        repository.loadStatePolygons();

        verify(logger).warn(anyString());
    }

    private double[] firstPointCoordinates(Path2D polygon) {
        double[] coordinates = new double[6];
        PathIterator iter = polygon.getPathIterator(null);
        iter.currentSegment(coordinates);
        return new double[]{coordinates[0], coordinates[1]};
    }

    private int polygonSize(Path2D polygon) {
        int i = 0;
        PathIterator iter = polygon.getPathIterator(null);
        while (!iter.isDone()) {
            iter.next();
            i++;
        }
        return i;
    }
}