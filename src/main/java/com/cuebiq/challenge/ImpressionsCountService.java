package com.cuebiq.challenge;

import com.cuebiq.challenge.model.ImpressionBean;
import com.cuebiq.challenge.model.StatePolygonBean;
import com.cuebiq.challenge.model.TimeSpan;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business logic to count the number of impressions and group them by a given criterion.
 */
@ApplicationScoped
public class ImpressionsCountService {

    @Inject
    ImpressionsCountRepository impressionsCountRepository;

    /**
     * Groups the impressions by time stamp and counts how many impressions fall into each bucket. See also {@link com.cuebiq.challenge.ImpressionsCountResource#getByTimespan(String) the related API endpoint}.
     *
     * @param timeSpan The time span id identifying the grouping criterion.
     * @return The number of impressions for each time span bucket.
     */
    Map<Integer, Long> getImpressionsCountByTimeSpan(TimeSpan timeSpan) throws IOException {
        return impressionsCountRepository.loadImpressions().collect(Collectors.groupingBy(timeSpan.getGroupKeyFn(), Collectors.counting()));
    }

    /**
     * Groups the impressions by device id and counts how many impressions fall into each bucket. See also {@link ImpressionsCountResource#getByDevice()} the related API endpoint}.
     *
     * @return The number of impressions for each device id.
     */
    Map<BigDecimal, Long> getImpressionsCountByDevice() throws IOException {
        return impressionsCountRepository.loadImpressions().collect(Collectors.groupingBy(ImpressionBean::getDeviceId, Collectors.counting()));
    }

    /**
     * Groups the impressions by US State and counts how many impressions fall into each bucket. Impressions that are not within a US State are ignored. See also {@link ImpressionsCountResource#getByUsState()} the related API endpoint}.
     *
     * Current implementation is quite slow. There's definitely room for improvement, but I guess it's beyond the scope of the challenge.
     *
     * @return The number of impressions for each US State id.
     */
    Map<String, Long> getImpressionsCountByUsState() throws IOException {
        List<StatePolygonBean> statePolygons = impressionsCountRepository.loadStatePolygons();
        return impressionsCountRepository.loadImpressions().map(impression -> findStateByImpression(impression, statePolygons)).filter(Optional::isPresent).collect(Collectors.groupingBy(Optional::get, Collectors.counting()));
    }

    private Optional<String> findStateByImpression(ImpressionBean impression, List<StatePolygonBean> statePolygons) {
        return statePolygons.stream().filter(statePolygon -> stateContainsImpression(statePolygon, impression)).findFirst().map(StatePolygonBean::getName);
    }

    private boolean stateContainsImpression(StatePolygonBean statePolygon, ImpressionBean impression) {
        boolean stateContainsImpression = statePolygon.getPolygon().contains(impression.getLatitude().doubleValue(), impression.getLongitude().doubleValue());

        if (stateContainsImpression) {
            for (Path2D exclusion : statePolygon.getExclusionPolygons()) {
                if (exclusion.contains(impression.getLatitude().doubleValue(), impression.getLongitude().doubleValue())) {
                    return false;
                }
            }
        }
        return stateContainsImpression;
    }

}
