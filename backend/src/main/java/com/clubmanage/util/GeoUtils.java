package com.clubmanage.util;

import java.math.BigDecimal;

public final class GeoUtils {

    private static final double EARTH_RADIUS_METERS = 6371000;

    private GeoUtils() {
    }

    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = radLat2 - radLat1;
        double deltaLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    public static double toDouble(BigDecimal value) {
        return value == null ? 0 : value.doubleValue();
    }
}