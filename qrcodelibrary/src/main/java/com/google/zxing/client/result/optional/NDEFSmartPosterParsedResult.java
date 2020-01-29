//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result.optional;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class NDEFSmartPosterParsedResult extends ParsedResult {
    public static final int ACTION_UNSPECIFIED = -1;
    public static final int ACTION_DO = 0;
    public static final int ACTION_SAVE = 1;
    public static final int ACTION_OPEN = 2;
    private final String title;
    private final String uri;
    private final int action;

    NDEFSmartPosterParsedResult(int action, String uri, String title) {
        super(ParsedResultType.NDEF_SMART_POSTER);
        this.action = action;
        this.uri = uri;
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public String getURI() {
        return this.uri;
    }

    public int getAction() {
        return this.action;
    }

    public String getDisplayResult() {
        return this.title == null?this.uri:this.title + '\n' + this.uri;
    }
}
