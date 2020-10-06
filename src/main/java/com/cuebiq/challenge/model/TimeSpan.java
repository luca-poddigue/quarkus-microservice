package com.cuebiq.challenge.model;

import com.cuebiq.challenge.ImpressionsCountResource;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Models the supported time spans to count impressions grouping by time spans.
 *
 * @see ImpressionsCountResource
 */
public enum TimeSpan {

    HOUR_OF_DAY(impressionBean -> impressionBean.getTimestamp().getHour(), "hod"),
    DAY_OF_WEEK(impressionBean -> impressionBean.getTimestamp().getDayOfWeek().getValue(), "dow"),
    DAY_OF_MONTH(impressionBean -> impressionBean.getTimestamp().getDayOfMonth(), "dom");

    private Function<ImpressionBean, Integer> groupKeyFn;
    private String id;

    /**
     * @param groupKeyFn The lambda function determining the grouping criterion
     * @param id         The time span id accepted by the API as a query param
     */
    TimeSpan(Function<ImpressionBean, Integer> groupKeyFn, String id) {
        this.groupKeyFn = Objects.requireNonNull(groupKeyFn);
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Finds the enum related to a given time stamp id.
     *
     * @param timeSpanId The API query param defining the time span id.
     * @return If found, an optional containing the related enum, an empty optional otherwise.
     */
    public static Optional<TimeSpan> getById(String timeSpanId) {
        return Arrays.stream(TimeSpan.values()).filter(timeSpan -> timeSpan.getId().equals(timeSpanId)).findFirst();
    }

    /**
     * @return The lambda function determining the grouping criterion
     */
    public Function<ImpressionBean, Integer> getGroupKeyFn() {
        return groupKeyFn;
    }

    /**
     * @return The time span id accepted by the API as a query param
     */
    public String getId() {
        return id;
    }

}