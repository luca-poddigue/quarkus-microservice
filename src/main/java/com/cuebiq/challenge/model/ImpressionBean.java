package com.cuebiq.challenge.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Models the impressions data contained in the dataset. In this application the time zone is not taken into account.
 */
@Getter
@RequiredArgsConstructor
public class ImpressionBean {

    private @NonNull BigDecimal deviceId;
    private @NonNull BigDecimal latitude;
    private @NonNull BigDecimal longitude;
    private @NonNull LocalDateTime timestamp;

}
