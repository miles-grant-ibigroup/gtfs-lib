package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import org.mapdb.Fun;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;

/**
 * Represents a GTFS StopTime. Note that once created and saved in a feed, stop times are by convention immutable
 * because they are in a MapDB.
 */
public class StopTime extends Entity implements Cloneable, Serializable {

    private static final long serialVersionUID = -8883780047901081832L;
    /* StopTime cannot directly reference Trips or Stops because they would be serialized into the MapDB. */
    public String trip_id;
    public int    arrival_time = INT_MISSING;
    public int    departure_time = INT_MISSING;
    public String stop_id;
    public int    stop_sequence;
    public String stop_headsign;
    public int    pickup_type;
    public int    drop_off_type;
    public int    continuous_pickup = INT_MISSING;
    public int    continuous_drop_off = INT_MISSING;
    public double shape_dist_traveled = DOUBLE_MISSING;
    public int    timepoint = INT_MISSING;

    @Override
    public String getId() {
        return trip_id; // Needs sequence number to be unique
    }

    @Override
    public Integer getSequenceNumber() {
        return stop_sequence; // Compound key of StopTime is (trip_id, stop_sequence)
    }

    /**
     * Sets the parameters for a prepared statement following the parameter order defined in
     * {@link com.conveyal.gtfs.loader.Table#STOP_TIMES}. JDBC prepared statement parameters use a one-based index.
     */
    @Override
    public void setStatementParameters(PreparedStatement statement, boolean setDefaultId) throws SQLException {
        int oneBasedIndex = 1;
        if (!setDefaultId) statement.setInt(oneBasedIndex++, id);
        statement.setString(oneBasedIndex++, trip_id);
        setIntParameter(statement, oneBasedIndex++, stop_sequence);
        statement.setString(oneBasedIndex++, stop_id);
        setIntParameter(statement, oneBasedIndex++, arrival_time);
        setIntParameter(statement, oneBasedIndex++, departure_time);
        statement.setString(oneBasedIndex++, stop_headsign);
        setIntParameter(statement, oneBasedIndex++, pickup_type);
        setIntParameter(statement, oneBasedIndex++, drop_off_type);
        setIntParameter(statement, oneBasedIndex++, continuous_pickup);
        setIntParameter(statement, oneBasedIndex++, continuous_drop_off);
        statement.setDouble(oneBasedIndex++, shape_dist_traveled);
        setIntParameter(statement, oneBasedIndex++, timepoint);
    }

    public static class Loader extends Entity.Loader<StopTime> {

        public Loader(GTFSFeed feed) {
            super(feed, "stop_times");
        }

        @Override
        protected boolean isRequired() {
            return true;
        }

        @Override
        public void loadOneRow() throws IOException {
            StopTime st = new StopTime();
            st.id = row + 1; // offset line number by 1 to account for 0-based row index
            st.trip_id        = getStringField("trip_id", true);
            // TODO: arrival_time and departure time are not required, but if one is present the other should be
            // also, if this is the first or last stop, they are both required
            st.arrival_time   = getTimeField("arrival_time", false);
            st.departure_time = getTimeField("departure_time", false);
            st.stop_id        = getStringField("stop_id", true);
            st.stop_sequence  = getIntField("stop_sequence", true, 0, Integer.MAX_VALUE);
            st.stop_headsign  = getStringField("stop_headsign", false);
            st.pickup_type    = getIntField("pickup_type", false, 0, 3); // TODO add ranges as parameters
            st.drop_off_type  = getIntField("drop_off_type", false, 0, 3);
            st.continuous_pickup = getIntField("continuous_pickup", true, 0, 3);
            st.continuous_pickup = getIntField("continuous_drop_off", true, 0, 3);
            st.shape_dist_traveled = getDoubleField("shape_dist_traveled", false, 0D, Double.MAX_VALUE); // FIXME using both 0 and NaN for "missing", define DOUBLE_MISSING
            st.timepoint      = getIntField("timepoint", false, 0, 1, INT_MISSING);
            st.feed           = null; // this could circular-serialize the whole feed
            feed.stop_times.put(new Fun.Tuple2(st.trip_id, st.stop_sequence), st);

            /*
              Check referential integrity without storing references. StopTime cannot directly reference Trips or
              Stops because they would be serialized into the MapDB.
             */
            getRefField("trip_id", true, feed.trips);
            getRefField("stop_id", true, feed.stops);
        }

    }

    public static class Writer extends Entity.Writer<StopTime> {
        public Writer (GTFSFeed feed) {
            super(feed, "stop_times");
        }

        @Override
        protected void writeHeaders() throws IOException {
            writer.writeRecord(new String[] {"trip_id", "arrival_time", "departure_time", "stop_id", "stop_sequence", "stop_headsign",
                    "pickup_type", "drop_off_type", "continuous_pickup", "continuous_drop_off", "shape_dist_traveled", "timepoint"});
        }

        @Override
        protected void writeOneRow(StopTime st) throws IOException {
            writeStringField(st.trip_id);
            writeTimeField(st.arrival_time);
            writeTimeField(st.departure_time);
            writeStringField(st.stop_id);
            writeIntField(st.stop_sequence);
            writeStringField(st.stop_headsign);
            writeIntField(st.pickup_type);
            writeIntField(st.drop_off_type);
            writeIntField(st.continuous_pickup);
            writeIntField(st.continuous_drop_off);
            writeDoubleField(st.shape_dist_traveled);
            writeIntField(st.timepoint);
            endRecord();
        }

        @Override
        protected Iterator<StopTime> iterator() {
            return feed.stop_times.values().iterator();
        }


    }

    @Override
    public StopTime clone () {
        try {
            return (StopTime) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StopTime stopTime = (StopTime) o;
        return arrival_time == stopTime.arrival_time &&
            departure_time == stopTime.departure_time &&
            stop_sequence == stopTime.stop_sequence &&
            pickup_type == stopTime.pickup_type &&
            drop_off_type == stopTime.drop_off_type &&
            Double.compare(stopTime.shape_dist_traveled, shape_dist_traveled) == 0 &&
            timepoint == stopTime.timepoint &&
            Objects.equals(trip_id, stopTime.trip_id) &&
            Objects.equals(stop_id, stopTime.stop_id) &&
            Objects.equals(stop_headsign, stopTime.stop_headsign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            trip_id,
            arrival_time,
            departure_time,
            stop_id,
            stop_sequence,
            stop_headsign,
            pickup_type,
            drop_off_type,
            shape_dist_traveled,
            timepoint
        );
    }
}
