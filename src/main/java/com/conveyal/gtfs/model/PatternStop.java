package com.conveyal.gtfs.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A pattern stop represents generalized information about a stop visited by a pattern, i.e. a collection of trips that
 * all visit the same stops in the same sequence. Some of these characteristics, e.g., stop ID, stop sequence, pickup
 * type, and drop off type, help determine a unique pattern. Others (default dwell/travel time, timepoint, and shape dist
 * traveled) are specific to the editor and usually based on values from the first trip encountered in a feed for a
 * given pattern.
 */
public class PatternStop extends Entity {
    private static final long serialVersionUID = 1L;

    public String pattern_id;
    public int stop_sequence;
    public String stop_id;
    // FIXME: Should we be storing default travel and dwell times here?
    public int default_travel_time;
    public int default_dwell_time;
    public double shape_dist_traveled;
    public int pickup_type;
    public int drop_off_type;
    public int timepoint;
    public int continuous_pickup = INT_MISSING;
    public int continuous_drop_off = INT_MISSING;

    // Flex additions.
    public String pickup_booking_rule_id;
    public String drop_off_booking_rule_id;

    // Additional GTFS Flex location groups and locations fields
    public String location_id;
    public int start_pickup_dropoff_window = INT_MISSING;
    public int end_pickup_dropoff_window = INT_MISSING;
    public double mean_duration_factor = DOUBLE_MISSING;
    public double mean_duration_offset = DOUBLE_MISSING;
    public double safe_duration_factor = DOUBLE_MISSING;
    public double safe_duration_offset = DOUBLE_MISSING;

    public PatternStop () {}

    /**
     * Sets the parameters for a prepared statement following the parameter order defined in
     * {@link com.conveyal.gtfs.loader.Table#PATTERN_STOP}. JDBC prepared statement parameters use a one-based index.
     */
    @Override
    public void setStatementParameters(PreparedStatement statement, boolean setDefaultId) throws SQLException {
        // FIXME
    }
}
