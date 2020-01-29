//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ResultParser;

final class GeoResultParser extends ResultParser {
    private GeoResultParser() {
    }

    public static GeoParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText == null || !rawText.startsWith("geo:") && !rawText.startsWith("GEO:")) {
            return null;
        } else {
            int queryStart = rawText.indexOf(63, 4);
            String query;
            String geoURIWithoutQuery;
            if(queryStart < 0) {
                query = null;
                geoURIWithoutQuery = rawText.substring(4);
            } else {
                query = rawText.substring(queryStart + 1);
                geoURIWithoutQuery = rawText.substring(4, queryStart);
            }

            int latitudeEnd = geoURIWithoutQuery.indexOf(44);
            if(latitudeEnd < 0) {
                return null;
            } else {
                int longitudeEnd = geoURIWithoutQuery.indexOf(44, latitudeEnd + 1);

                double latitude;
                double longitude;
                double altitude;
                try {
                    latitude = Double.parseDouble(geoURIWithoutQuery.substring(0, latitudeEnd));
                    if(latitude > 90.0D || latitude < -90.0D) {
                        return null;
                    }

                    if(longitudeEnd < 0) {
                        longitude = Double.parseDouble(geoURIWithoutQuery.substring(latitudeEnd + 1));
                        altitude = 0.0D;
                    } else {
                        longitude = Double.parseDouble(geoURIWithoutQuery.substring(latitudeEnd + 1, longitudeEnd));
                        altitude = Double.parseDouble(geoURIWithoutQuery.substring(longitudeEnd + 1));
                    }

                    if(longitude > 180.0D || longitude < -180.0D || altitude < 0.0D) {
                        return null;
                    }
                } catch (NumberFormatException var14) {
                    return null;
                }

                return new GeoParsedResult(latitude, longitude, altitude, query);
            }
        }
    }
}
