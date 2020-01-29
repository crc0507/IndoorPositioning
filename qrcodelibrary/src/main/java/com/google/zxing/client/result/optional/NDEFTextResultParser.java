//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result.optional;

import com.google.zxing.Result;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.optional.AbstractNDEFResultParser;
import com.google.zxing.client.result.optional.NDEFRecord;

final class NDEFTextResultParser extends AbstractNDEFResultParser {
    NDEFTextResultParser() {
    }

    public static TextParsedResult parse(Result result) {
        byte[] bytes = result.getRawBytes();
        if(bytes == null) {
            return null;
        } else {
            NDEFRecord ndefRecord = NDEFRecord.readRecord(bytes, 0);
            if(ndefRecord != null && ndefRecord.isMessageBegin() && ndefRecord.isMessageEnd()) {
                if(!ndefRecord.getType().equals("T")) {
                    return null;
                } else {
                    String[] languageText = decodeTextPayload(ndefRecord.getPayload());
                    return new TextParsedResult(languageText[0], languageText[1]);
                }
            } else {
                return null;
            }
        }
    }

    static String[] decodeTextPayload(byte[] payload) {
        byte statusByte = payload[0];
        boolean isUTF16 = (statusByte & 128) != 0;
        int languageLength = statusByte & 31;
        String language = bytesToString(payload, 1, languageLength, "US-ASCII");
        String encoding = isUTF16?"UTF-16":"UTF8";
        String text = bytesToString(payload, 1 + languageLength, payload.length - languageLength - 1, encoding);
        return new String[]{language, text};
    }
}
