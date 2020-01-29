//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.WifiParsedResult;

final class WifiResultParser extends ResultParser {
    private WifiResultParser() {
    }

    public static WifiParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.startsWith("WIFI:")) {
            boolean trim = false;
            String ssid = matchSinglePrefixedField("S:", rawText, ';', trim);
            String pass = matchSinglePrefixedField("P:", rawText, ';', trim);
            String type = matchSinglePrefixedField("T:", rawText, ';', trim);
            return new WifiParsedResult(type, ssid, pass);
        } else {
            return null;
        }
    }
}
