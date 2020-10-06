package com.cuebiq.challenge.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

class TimeSpanTest {

    @Test
    void givenATimeSpan_whenGettingId_shouldReturnTheId() {
        assertThat(TimeSpan.HOUR_OF_DAY.getId(), is(equalTo("hod")));
    }

    @Test
    void givenATimeSpan_whenGettingGroupingFunction_shouldReturnTheGroupingFunctionForTheTimeSpan() {
        final int EXPECTED_HOUR_OF_DAY = 5;
        final int EXPECTED_DAY_OF_MONTH = 1;
        final int EXPECTED_DAY_OF_WEEK = 2;

        ImpressionBean impression = createImpression(EXPECTED_DAY_OF_MONTH, EXPECTED_HOUR_OF_DAY);

        assertThat(TimeSpan.HOUR_OF_DAY.getGroupKeyFn().apply(impression), is(equalTo(EXPECTED_HOUR_OF_DAY)));
        assertThat(TimeSpan.DAY_OF_MONTH.getGroupKeyFn().apply(impression), is(equalTo(EXPECTED_DAY_OF_MONTH)));
        assertThat(TimeSpan.DAY_OF_WEEK.getGroupKeyFn().apply(impression), is(equalTo(EXPECTED_DAY_OF_WEEK)));
    }

    @Test
    void givenAnId_whenGettingTimeSpanById_shouldReturnTheRightTimeStamp() {
        assertThat(TimeSpan.getById("hod").get(), is(equalTo(TimeSpan.HOUR_OF_DAY)));
    }

    private ImpressionBean createImpression(int dayOfMonth, int hourOfDay) {
        return new ImpressionBean(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                LocalDateTime.of(2019, 1, dayOfMonth, hourOfDay, 0));
    }
}