package com.youtility.intelliwiz20.Interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by PrashantD on 27/1/18.
 *
 * used in area and branch manager attendance sheet report
 */

public interface IMapClickedLocation {
    void getClickedLocation(LatLng latLng);
}
