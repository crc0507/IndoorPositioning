//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class URIParsedResult extends ParsedResult {
    private final String uri;
    private final String title;

    public URIParsedResult(String uri, String title) {
        super(ParsedResultType.URI);
        this.uri = massageURI(uri);
        this.title = title;
    }

    public String getURI() {
        return this.uri;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isPossiblyMaliciousURI() {
        return this.containsUser();
    }

    private boolean containsUser() {
        int hostStart = this.uri.indexOf(58);
        ++hostStart;

        int uriLength;
        for(uriLength = this.uri.length(); hostStart < uriLength && this.uri.charAt(hostStart) == 47; ++hostStart) {
            ;
        }

        int hostEnd = this.uri.indexOf(47, hostStart);
        if(hostEnd < 0) {
            hostEnd = uriLength;
        }

        int at = this.uri.indexOf(64, hostStart);
        return at >= hostStart && at < hostEnd;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(30);
        maybeAppend(this.title, result);
        maybeAppend(this.uri, result);
        return result.toString();
    }

    private static String massageURI(String uri) {
        int protocolEnd = uri.indexOf(58);
        if(protocolEnd < 0) {
            uri = "http://" + uri;
        } else if(isColonFollowedByPortNumber(uri, protocolEnd)) {
            uri = "http://" + uri;
        } else {
            uri = uri.substring(0, protocolEnd).toLowerCase() + uri.substring(protocolEnd);
        }

        return uri;
    }

    private static boolean isColonFollowedByPortNumber(String uri, int protocolEnd) {
        int nextSlash = uri.indexOf(47, protocolEnd + 1);
        if(nextSlash < 0) {
            nextSlash = uri.length();
        }

        if(nextSlash <= protocolEnd + 1) {
            return false;
        } else {
            for(int x = protocolEnd + 1; x < nextSlash; ++x) {
                if(uri.charAt(x) < 48 || uri.charAt(x) > 57) {
                    return false;
                }
            }

            return true;
        }
    }
}
