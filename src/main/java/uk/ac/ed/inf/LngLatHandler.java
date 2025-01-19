package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
public class LngLatHandler implements LngLatHandling{
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double startLng = startPosition.lng();
        double startLat = startPosition.lat();

        double endLng = endPosition.lng();
        double endLat = endPosition.lat();

        return Math.sqrt( Math.pow( ( startLng - endLng ), 2 ) + Math.pow( ( startLat - endLat ), 2 ));
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        double distance = distanceTo(startPosition, otherPosition);
        return distance < 0.00015;
    }

    @Override
    // USING Ray casting algorithm
    public boolean isInRegion(LngLat position, NamedRegion region) {
        int count = 0;
        double curPosLng = position.lng();
        double curPosLat = position.lat();
        LngLat[] points = region.vertices();

        for (int i = 0; i < points.length; i = i + 1) {
            double pt1Lng = points[i].lng();
            double pt1Lat = points[i].lat();

            double pt2Lng;
            double pt2Lat;

            if (i < points.length - 1) {
                pt2Lng = points[i + 1].lng();
                pt2Lat = points[i + 1].lat();
            }
            else {
                pt2Lng = points[0].lng();
                pt2Lat = points[0].lat();
            }

            if ( (curPosLat < pt1Lat) != (curPosLat < pt2Lat) &&
                (curPosLng < pt1Lng + ((curPosLat - pt1Lat) / (pt2Lat - pt1Lat)) * (pt2Lng - pt1Lng)))
            {
                count += 1;
            }
        }

        return count % 2 == 1;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {

        if (angle == 999)
        {
            return startPosition;
        }

        double lng = startPosition.lng() + 0.00015 * Math.cos(Math.toRadians(angle));
        double lat = startPosition.lat() + 0.00015 * Math.sin(Math.toRadians(angle));

        return new LngLat(lng,lat);
    }
}
