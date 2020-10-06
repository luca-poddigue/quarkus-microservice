package com.cuebiq.challenge;

import com.cuebiq.challenge.model.ImpressionBean;
import com.cuebiq.challenge.model.StatePolygonBean;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Manages all the accesses to data, in this case resource files. Some sort of caching
 * could be implemented here, but it actually doesn't give any added value in this specific setting.
 */
@ApplicationScoped
public class ImpressionsCountRepository {

    private Logger logger = LoggerFactory.getLogger(ImpressionsCountRepository.class);

    @ConfigProperty(name = "impressionsCount.datasetFileName")
    String datasetFileName;
    @ConfigProperty(name = "impressionsCount.statesMapFileName")
    String statesMapFileName;

    /**
     * Reads the CSV resource file and converts each record to an ImpressionBean.
     *
     * @return A stream of impression beans
     * @throws IOException If the resource file cannot be read.
     */
    Stream<ImpressionBean> loadImpressions() throws IOException {
        CSVParser csvParser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new InputStreamReader(ImpressionsCountRepository.class.getResourceAsStream(datasetFileName)));
        return StreamSupport.stream(csvParser.spliterator(), false).map((this::toImpressionBean));
    }

    private ImpressionBean toImpressionBean(CSVRecord csvRecord) {
        BigDecimal deviceId = new BigDecimal(csvRecord.get(0));
        BigDecimal latitude = new BigDecimal(csvRecord.get(1));
        BigDecimal longitude = new BigDecimal(csvRecord.get(2));
        LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(csvRecord.get(3))), ZoneOffset.UTC);
        return new ImpressionBean(deviceId, latitude, longitude, timestamp);
    }

    /**
     * Reads the GeoJSON resource file and converts them to a list of StatePolygonBean. Each bean contains
     * the name of the state, and a polygon model that can be used to check if an impression is contained
     * within it. Holes are also considered and added to the polygon model. MultiPolygons related to a state
     * are flattened, so the final list can contain more than one StatePolygonBean for each state.
     *
     * @return A stream of state polygon beans
     */
    List<StatePolygonBean> loadStatePolygons() {
        JSONTokener tokener = new JSONTokener(ImpressionsCountService.class.getResourceAsStream(statesMapFileName));
        JSONObject map = new JSONObject(tokener);
        JSONArray usStates = map.getJSONArray("features");

        List<StatePolygonBean> statePolygons = new ArrayList<>();
        for (int i = 0; i < usStates.length(); i++) {
            JSONObject state = usStates.getJSONObject(i);
            String stateName = state.getJSONObject("properties").getString("level1");

            JSONObject geometry = state.getJSONObject("geometry");
            String geometryType = geometry.getString("type");
            JSONArray coordinates = geometry.getJSONArray("coordinates");
            switch (geometryType) {
                case "MultiPolygon":
                    for (int j = 0; j < coordinates.length(); j++) {
                        statePolygons.add(buildStatePolygonBean(stateName, coordinates.getJSONArray(j)));
                    }
                    break;
                case "Polygon":
                    statePolygons.add(buildStatePolygonBean(stateName, coordinates));
                    break;
                default:
                    logger.warn("Unexpected geometry type: " + geometryType);
            }
        }
        return statePolygons;
    }

    private StatePolygonBean buildStatePolygonBean(String stateName, JSONArray coordinates) {
        Path2D polygon = buildPolygon(coordinates, 0);
        List<Path2D> exclusionPolygons = new ArrayList<>();
        if (coordinates.length() > 1) {
            for (int i = 1; i < coordinates.length(); i++) {
                exclusionPolygons.add(buildPolygon(coordinates, i));
            }
        }
        return new StatePolygonBean(stateName, polygon, exclusionPolygons);
    }

    private Path2D buildPolygon(JSONArray coordinates, int index) {
        JSONArray polygonPoints = coordinates.getJSONArray(index);
        Path2D polygon = new Path2D.Double();
        for (int i = 0; i < polygonPoints.length(); i++) {
            JSONArray polygonPoint = polygonPoints.getJSONArray(i);
            if (i == 0) {
                polygon.moveTo(polygonPoint.getDouble(0), polygonPoint.getDouble(1));
            } else {
                polygon.lineTo(polygonPoint.getDouble(0), polygonPoint.getDouble(1));
            }
        }
        return polygon;
    }

}
