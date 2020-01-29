//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ResultParser;

public class ISBNResultParser extends ResultParser {
    private ISBNResultParser() {
    }

    public static ISBNParsedResult parse(Result result) {
        BarcodeFormat format = result.getBarcodeFormat();
        if(!BarcodeFormat.EAN_13.equals(format)) {
            return null;
        } else {
            String rawText = result.getText();
            if(rawText == null) {
                return null;
            } else {
                int length = rawText.length();
                return length != 13?null:(!rawText.startsWith("978") && !rawText.startsWith("979")?null:new ISBNParsedResult(rawText));
            }
        }
    }
}
