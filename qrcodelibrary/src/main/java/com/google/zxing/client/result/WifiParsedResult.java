//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class WifiParsedResult extends ParsedResult {
    private final String ssid;
    private final String networkEncryption;
    private final String password;

    public WifiParsedResult(String networkEncryption, String ssid, String password) {
        super(ParsedResultType.WIFI);
        this.ssid = ssid;
        this.networkEncryption = networkEncryption;
        this.password = password;
    }

    public String getSsid() {
        return this.ssid;
    }

    public String getNetworkEncryption() {
        return this.networkEncryption;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(80);
        maybeAppend(this.ssid, result);
        maybeAppend(this.networkEncryption, result);
        maybeAppend(this.password, result);
        return result.toString();
    }
}
