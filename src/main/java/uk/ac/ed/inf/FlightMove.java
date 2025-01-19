package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

//Object Created for storing Flight Move Details
public record FlightMove (LngLat fromLngLat, double angle, LngLat toLngLat) {

    public FlightMove (LngLat fromLngLat, double angle, LngLat toLngLat) {
        this.fromLngLat = fromLngLat;
        this.angle = angle;
        this.toLngLat = toLngLat;
    }

    public LngLat fromLngLat() { return this.fromLngLat; }

    public double angle() { return this.angle; }

    public LngLat toLngLat() { return this.toLngLat; }
}
