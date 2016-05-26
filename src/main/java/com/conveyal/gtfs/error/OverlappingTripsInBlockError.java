package com.conveyal.gtfs.error;

import com.conveyal.gtfs.model.Route;
import com.conveyal.gtfs.validator.model.Priority;

/**
 * Created by landon on 5/6/16.
 */
public class OverlappingTripsInBlockError extends GTFSError {

    public String[] tripIds;
    public String affectedEntityId;
    public Priority priority = Priority.HIGH;

    public OverlappingTripsInBlockError(long line, String field, String affectedEntityId, Route route, String[] tripIds, Priority priority) {
        super("trips", line, field);
        this.tripIds = tripIds;
        this.affectedEntityId = affectedEntityId;
    }

    @Override public String getMessage() {
        return "Trip Ids " + String.join(" & ", tripIds) + " overlap and share block Id " + affectedEntityId;
    }
}