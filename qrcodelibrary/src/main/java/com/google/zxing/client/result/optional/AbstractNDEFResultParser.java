//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result.optional;

import com.google.zxing.client.result.ResultParser;
import java.io.UnsupportedEncodingException;

abstract class AbstractNDEFResultParser extends ResultParser {
    AbstractNDEFResultParser() {
    }

    static String bytesToString(byte[] bytes, int offset, int length, String encoding) {
        try {
            return new String(bytes, offset, length, encoding);
        } catch (UnsupportedEncodingException var5) {
            throw new RuntimeException("Platform does not support required encoding: " + var5);
        }
    }
}
