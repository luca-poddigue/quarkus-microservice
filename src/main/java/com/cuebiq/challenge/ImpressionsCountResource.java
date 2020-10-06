package com.cuebiq.challenge;

import com.cuebiq.challenge.model.TimeSpan;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * API for counting impressions and grouping them by specific criterion.
 */
@Path("/api/impressionsCount")
public class ImpressionsCountResource {

    @Inject
    ImpressionsCountService service;

    /**
     * Counts the number of impressions within each time span.<br>
     * Allowed time spans are:
     * <ul>
     * <li><strong>hod</strong>: hour of day, from 0 to 23
     * <li><strong>dow</strong>: day of week, from 1 to 7, where 1 means Monday
     * <li><strong>dom</strong>: day of month, from 1 to 31
     * </ul>
     *
     * @return The number of impressions, grouped by the chosen time span
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/byTimeSpan/{timeSpanId}")
    public Map<Integer, Long> getByTimespan(@PathParam("timeSpanId") String timeSpanId) throws IOException {
        Optional<TimeSpan> timeSpan = TimeSpan.getById(timeSpanId.toLowerCase());
        if (timeSpan.isPresent()) {
            return service.getImpressionsCountByTimeSpan(timeSpan.get());
        } else {
            throw new BadRequestException("Invalid time span id: " + timeSpanId);
        }
    }

    /**
     * Counts the number of impressions for each device id.
     *
     * @return The number of impressions, grouped by device id
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/byDevice")
    public Map<BigDecimal, Long> getByDevice() throws IOException {
        return service.getImpressionsCountByDevice();
    }

    /**
     * Counts the number of impressions for each US State.
     *
     * @return The number of impressions, grouped by US State
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/byUsState")
    public Map<String, Long> getByUsState() throws IOException {
        return service.getImpressionsCountByUsState();
    }

}