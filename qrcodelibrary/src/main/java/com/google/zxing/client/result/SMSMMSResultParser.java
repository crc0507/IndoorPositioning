//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.SMSParsedResult;
import java.util.Hashtable;
import java.util.Vector;

final class SMSMMSResultParser extends ResultParser {
    private SMSMMSResultParser() {
    }

    public static SMSParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText == null) {
            return null;
        } else if(!rawText.startsWith("sms:") && !rawText.startsWith("SMS:") && !rawText.startsWith("mms:") && !rawText.startsWith("MMS:")) {
            return null;
        } else {
            Hashtable nameValuePairs = parseNameValuePairs(rawText);
            String subject = null;
            String body = null;
            boolean querySyntax = false;
            if(nameValuePairs != null && !nameValuePairs.isEmpty()) {
                subject = (String)nameValuePairs.get("subject");
                body = (String)nameValuePairs.get("body");
                querySyntax = true;
            }

            int queryStart = rawText.indexOf(63, 4);
            String smsURIWithoutQuery;
            if(queryStart >= 0 && querySyntax) {
                smsURIWithoutQuery = rawText.substring(4, queryStart);
            } else {
                smsURIWithoutQuery = rawText.substring(4);
            }

            int lastComma = -1;
            Vector numbers = new Vector(1);

            int comma;
            Vector vias;
            for(vias = new Vector(1); (comma = smsURIWithoutQuery.indexOf(44, lastComma + 1)) > lastComma; lastComma = comma) {
                String numberPart = smsURIWithoutQuery.substring(lastComma + 1, comma);
                addNumberVia(numbers, vias, numberPart);
            }

            addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1));
            return new SMSParsedResult(toStringArray(numbers), toStringArray(vias), subject, body);
        }
    }

    private static void addNumberVia(Vector numbers, Vector vias, String numberPart) {
        int numberEnd = numberPart.indexOf(59);
        if(numberEnd < 0) {
            numbers.addElement(numberPart);
            vias.addElement((Object)null);
        } else {
            numbers.addElement(numberPart.substring(0, numberEnd));
            String maybeVia = numberPart.substring(numberEnd + 1);
            String via;
            if(maybeVia.startsWith("via=")) {
                via = maybeVia.substring(4);
            } else {
                via = null;
            }

            vias.addElement(via);
        }

    }
}
