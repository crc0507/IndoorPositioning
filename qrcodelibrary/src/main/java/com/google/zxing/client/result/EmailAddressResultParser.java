//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.EmailDoCoMoResultParser;
import com.google.zxing.client.result.ResultParser;
import java.util.Hashtable;

final class EmailAddressResultParser extends ResultParser {
    EmailAddressResultParser() {
    }

    public static EmailAddressParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText == null) {
            return null;
        } else if(!rawText.startsWith("mailto:") && !rawText.startsWith("MAILTO:")) {
            return !EmailDoCoMoResultParser.isBasicallyValidEmailAddress(rawText)?null:new EmailAddressParsedResult(rawText, (String)null, (String)null, "mailto:" + rawText);
        } else {
            String emailAddress = rawText.substring(7);
            int queryStart = emailAddress.indexOf(63);
            if(queryStart >= 0) {
                emailAddress = emailAddress.substring(0, queryStart);
            }

            Hashtable nameValues = parseNameValuePairs(rawText);
            String subject = null;
            String body = null;
            if(nameValues != null) {
                if(emailAddress.length() == 0) {
                    emailAddress = (String)nameValues.get("to");
                }

                subject = (String)nameValues.get("subject");
                body = (String)nameValues.get("body");
            }

            return new EmailAddressParsedResult(emailAddress, subject, body, rawText);
        }
    }
}
