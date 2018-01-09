package ke.co.thinksynergy.movers.utils;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Anthony Ngure on 08/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class MapHelper {

    /**
     * To zoom the camera to the best possible position so the camera can view
     * all the markers
     *
     * @param markers : The array of markers to zoom to
     */
    public static void zoomToFitMarkers(GoogleMap googleMap, Marker... markers) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (Marker m : markers) {
            b.include(m.getPosition());
        }
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 12, 12, 16);
        googleMap.animateCamera(cu);
    }

}
