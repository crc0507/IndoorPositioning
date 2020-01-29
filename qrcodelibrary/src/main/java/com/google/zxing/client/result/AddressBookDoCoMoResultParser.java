//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.AbstractDoCoMoResultParser;
import com.google.zxing.client.result.AddressBookParsedResult;

final class AddressBookDoCoMoResultParser extends AbstractDoCoMoResultParser {
    AddressBookDoCoMoResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.startsWith("MECARD:")) {
            String[] rawName = matchDoCoMoPrefixedField("N:", rawText, true);
            if(rawName == null) {
                return null;
            } else {
                String name = parseName(rawName[0]);
                String pronunciation = matchSingleDoCoMoPrefixedField("SOUND:", rawText, true);
                String[] phoneNumbers = matchDoCoMoPrefixedField("TEL:", rawText, true);
                String[] emails = matchDoCoMoPrefixedField("EMAIL:", rawText, true);
                String note = matchSingleDoCoMoPrefixedField("NOTE:", rawText, false);
                String[] addresses = matchDoCoMoPrefixedField("ADR:", rawText, true);
                String birthday = matchSingleDoCoMoPrefixedField("BDAY:", rawText, true);
                if(birthday != null && !isStringOfDigits(birthday, 8)) {
                    birthday = null;
                }

                String url = matchSingleDoCoMoPrefixedField("URL:", rawText, true);
                String org = matchSingleDoCoMoPrefixedField("ORG:", rawText, true);
                return new AddressBookParsedResult(maybeWrap(name), pronunciation, phoneNumbers, emails, note, addresses, org, birthday, (String)null, url);
            }
        } else {
            return null;
        }
    }

    private static String parseName(String name) {
        int comma = name.indexOf(44);
        return comma >= 0?name.substring(comma + 1) + ' ' + name.substring(0, comma):name;
    }
}
