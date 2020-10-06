package com.cuebiq.challenge.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.geom.Path2D;
import java.util.List;

/**
 * Models a polygon with holes and links it to its related State.
 */
@Getter
@RequiredArgsConstructor
public class StatePolygonBean {

    private @NonNull String name;
    private @NonNull Path2D polygon;
    private @NonNull List<Path2D> exclusionPolygons;

}