package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;

public class Location extends Entity {

    private static final long serialVersionUID = -3961639608144161095L;

    public String location_id;
    public String location_stop_name;
    public String zone_id;
    public URL location_stop_url;

    @Override
    public String getId() {
        return location_id;
    }

    /**
     * Sets the parameters for a prepared statement following the parameter order defined in
     * {@link com.conveyal.gtfs.loader.Table#LOCATIONS}. JDBC prepared statement parameters use a one-based index.
     */
    @Override
    public void setStatementParameters(PreparedStatement statement, boolean setDefaultId) throws SQLException {
        int oneBasedIndex = 1;
        if (!setDefaultId) statement.setInt(oneBasedIndex++, id);
        statement.setString(oneBasedIndex++, location_id);
        statement.setString(oneBasedIndex++, location_stop_name);
        statement.setString(oneBasedIndex++, zone_id);
        statement.setString(oneBasedIndex++, location_stop_url != null ? location_stop_url.toString() : null);
    }

    public static class Loader extends Entity.Loader<Location> {

        public Loader(GTFSFeed feed) {
            super(feed, "locations");
        }

        @Override
        protected boolean isRequired() {
            return false;
        }

        @Override
        public void loadOneRow() throws IOException {
            Location location = new Location();

            location.id = row + 1;
            location.location_stop_name = getStringField("location_stop_name", false);
            location.zone_id = getStringField("zone_id", false);
            location.location_stop_url = getUrlField("location_stop_url", false);

            // Attempting to put a null key or value will cause an NPE in BTreeMap
            if (location.location_id != null) {
                feed.locations.put(location.location_id, location);
            }

            /*
              Check referential integrity without storing references. Location cannot directly reference Calenders
              because they would be serialized into the MapDB.
             */
//            getRefField("location_id", true, feed.);
        }
    }

    public static class Writer extends Entity.Writer<Location> {
        public Writer(GTFSFeed feed) {
            super(feed, "locations");
        }

        @Override
        public void writeHeaders() throws IOException {
            writer.writeRecord(new String[]{"location_id", "location_stop_name", "zone_id", "location_stop_url"});
        }

        @Override
        public void writeOneRow(Location locations) throws IOException {
            writeStringField(locations.location_id);
            writeStringField(locations.zone_id);
            writeStringField(locations.location_stop_name);
            writeUrlField(locations.location_stop_url);
            endRecord();
        }

        @Override
        public Iterator<Location> iterator() {
            return this.feed.locations.values().iterator();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return location_stop_name == that.location_stop_name &&
                zone_id == that.zone_id &&
                Objects.equals(location_stop_url, that.location_stop_url) &&
                Objects.equals(location_id, that.location_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                location_id,
                location_stop_name,
                location_stop_url,
                zone_id
        );
    }
}
