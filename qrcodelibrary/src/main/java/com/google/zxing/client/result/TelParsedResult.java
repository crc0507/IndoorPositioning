//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class TelParsedResult extends ParsedResult {
    private final String number;
    private final String telURI;
    private final String title;

    public TelParsedResult(String number, String telURI, String title) {
        super(ParsedResultType.TEL);
        this.number = number;
        this.telURI = telURI;
        this.title = title;
    }

    public String getNumber() {
        return this.number;
    }

    public String getTelURI() {
        return this.telURI;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(20);
        maybeAppend(this.number, result);
        maybeAppend(this.title, result);
        return result.toString();
    }
}
