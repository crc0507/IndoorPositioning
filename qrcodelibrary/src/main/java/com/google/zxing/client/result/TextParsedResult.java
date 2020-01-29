//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class TextParsedResult extends ParsedResult {
    private final String text;
    private final String language;

    public TextParsedResult(String text, String language) {
        super(ParsedResultType.TEXT);
        this.text = text;
        this.language = language;
    }

    public String getText() {
        return this.text;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getDisplayResult() {
        return this.text;
    }
}
