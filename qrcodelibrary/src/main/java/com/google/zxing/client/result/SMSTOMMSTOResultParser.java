//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.SMSParsedResult;

final class SMSTOMMSTOResultParser extends ResultParser {
    private SMSTOMMSTOResultParser() {
    }

    public static SMSParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText == null) {
            return null;
        } else if(!rawText.startsWith("smsto:") && !rawText.startsWith("SMSTO:") && !rawText.startsWith("mmsto:") && !rawText.startsWith("MMSTO:")) {
            return null;
        } else {
            String number = rawText.substring(6);
            String body = null;
            int bodyStart = number.indexOf(58);
            if(bodyStart >= 0) {
                body = number.substring(bodyStart + 1);
                number = number.substring(0, bodyStart);
            }

            return new SMSParsedResult(number, (String)null, (String)null, body);
        }
    }
}
