//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result.optional;

import com.google.zxing.Result;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.client.result.optional.AbstractNDEFResultParser;
import com.google.zxing.client.result.optional.NDEFRecord;

final class NDEFURIResultParser extends AbstractNDEFResultParser {
    private static final String[] URI_PREFIXES = new String[]{null, "http://www.", "https://www.", "http://", "https://", "tel:", "mailto:", "ftp://anonymous:anonymous@", "ftp://ftp.", "ftps://", "sftp://", "smb://", "nfs://", "ftp://", "dav://", "news:", "telnet://", "imap:", "rtsp://", "urn:", "pop:", "sip:", "sips:", "tftp:", "btspp://", "btl2cap://", "btgoep://", "tcpobex://", "irdaobex://", "file://", "urn:epc:id:", "urn:epc:tag:", "urn:epc:pat:", "urn:epc:raw:", "urn:epc:", "urn:nfc:"};

    NDEFURIResultParser() {
    }

    public static URIParsedResult parse(Result result) {
        byte[] bytes = result.getRawBytes();
        if(bytes == null) {
            return null;
        } else {
            NDEFRecord ndefRecord = NDEFRecord.readRecord(bytes, 0);
            if(ndefRecord != null && ndefRecord.isMessageBegin() && ndefRecord.isMessageEnd()) {
                if(!ndefRecord.getType().equals("U")) {
                    return null;
                } else {
                    String fullURI = decodeURIPayload(ndefRecord.getPayload());
                    return new URIParsedResult(fullURI, (String)null);
                }
            } else {
                return null;
            }
        }
    }

    static String decodeURIPayload(byte[] payload) {
        int identifierCode = payload[0] & 255;
        String prefix = null;
        if(identifierCode < URI_PREFIXES.length) {
            prefix = URI_PREFIXES[identifierCode];
        }

        String restOfURI = bytesToString(payload, 1, payload.length - 1, "UTF8");
        return prefix == null?restOfURI:prefix + restOfURI;
    }
}
