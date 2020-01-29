//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.oned.UPCEReader;

final class ProductResultParser extends ResultParser {
    private ProductResultParser() {
    }

    public static ProductParsedResult parse(Result result) {
        BarcodeFormat format = result.getBarcodeFormat();
        if(!BarcodeFormat.UPC_A.equals(format) && !BarcodeFormat.UPC_E.equals(format) && !BarcodeFormat.EAN_8.equals(format) && !BarcodeFormat.EAN_13.equals(format)) {
            return null;
        } else {
            String rawText = result.getText();
            if(rawText == null) {
                return null;
            } else {
                int length = rawText.length();

                for(int normalizedProductID = 0; normalizedProductID < length; ++normalizedProductID) {
                    char c = rawText.charAt(normalizedProductID);
                    if(c < 48 || c > 57) {
                        return null;
                    }
                }

                String var6;
                if(BarcodeFormat.UPC_E.equals(format)) {
                    var6 = UPCEReader.convertUPCEtoUPCA(rawText);
                } else {
                    var6 = rawText;
                }

                return new ProductParsedResult(rawText, var6);
            }
        }
    }
}
