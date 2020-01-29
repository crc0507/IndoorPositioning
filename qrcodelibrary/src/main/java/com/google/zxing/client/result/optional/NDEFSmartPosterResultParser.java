//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result.optional;

import com.google.zxing.Result;
import com.google.zxing.client.result.optional.AbstractNDEFResultParser;
import com.google.zxing.client.result.optional.NDEFRecord;
import com.google.zxing.client.result.optional.NDEFSmartPosterParsedResult;
import com.google.zxing.client.result.optional.NDEFTextResultParser;
import com.google.zxing.client.result.optional.NDEFURIResultParser;

final class NDEFSmartPosterResultParser extends AbstractNDEFResultParser {
    NDEFSmartPosterResultParser() {
    }

    public static NDEFSmartPosterParsedResult parse(Result result) {
        byte[] bytes = result.getRawBytes();
        if(bytes == null) {
            return null;
        } else {
            NDEFRecord headerRecord = NDEFRecord.readRecord(bytes, 0);
            if(headerRecord != null && headerRecord.isMessageBegin() && headerRecord.isMessageEnd()) {
                if(!headerRecord.getType().equals("Sp")) {
                    return null;
                } else {
                    int offset = 0;
                    int recordNumber = 0;
                    NDEFRecord ndefRecord = null;
                    byte[] payload = headerRecord.getPayload();
                    byte action = -1;
                    String title = null;

                    String uri;
                    for(uri = null; offset < payload.length && (ndefRecord = NDEFRecord.readRecord(payload, offset)) != null; offset += ndefRecord.getTotalRecordLength()) {
                        if(recordNumber == 0 && !ndefRecord.isMessageBegin()) {
                            return null;
                        }

                        String type = ndefRecord.getType();
                        if("T".equals(type)) {
                            String[] languageText = NDEFTextResultParser.decodeTextPayload(ndefRecord.getPayload());
                            title = languageText[1];
                        } else if("U".equals(type)) {
                            uri = NDEFURIResultParser.decodeURIPayload(ndefRecord.getPayload());
                        } else if("act".equals(type)) {
                            action = ndefRecord.getPayload()[0];
                        }

                        ++recordNumber;
                    }

                    return recordNumber != 0 && (ndefRecord == null || ndefRecord.isMessageEnd())?new NDEFSmartPosterParsedResult(action, uri, title):null;
                }
            } else {
                return null;
            }
        }
    }
}
