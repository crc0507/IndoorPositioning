//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResultType;

public abstract class ParsedResult {
    private final ParsedResultType type;

    protected ParsedResult(ParsedResultType type) {
        this.type = type;
    }

    public ParsedResultType getType() {
        return this.type;
    }

    public abstract String getDisplayResult();

    public String toString() {
        return this.getDisplayResult();
    }

    public static void maybeAppend(String value, StringBuffer result) {
        if(value != null && value.length() > 0) {
            if(result.length() > 0) {
                result.append('\n');
            }

            result.append(value);
        }

    }

    public static void maybeAppend(String[] value, StringBuffer result) {
        if(value != null) {
            for(int i = 0; i < value.length; ++i) {
                if(value[i] != null && value[i].length() > 0) {
                    if(result.length() > 0) {
                        result.append('\n');
                    }

                    result.append(value[i]);
                }
            }
        }

    }
}
