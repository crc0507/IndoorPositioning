//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ResultParser;
import java.util.Vector;

final class AddressBookAUResultParser extends ResultParser {
    AddressBookAUResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.indexOf("MEMORY") >= 0 && rawText.indexOf("\r\n") >= 0) {
            String name = matchSinglePrefixedField("NAME1:", rawText, '\r', true);
            String pronunciation = matchSinglePrefixedField("NAME2:", rawText, '\r', true);
            String[] phoneNumbers = matchMultipleValuePrefix("TEL", 3, rawText, true);
            String[] emails = matchMultipleValuePrefix("MAIL", 3, rawText, true);
            String note = matchSinglePrefixedField("MEMORY:", rawText, '\r', false);
            String address = matchSinglePrefixedField("ADD:", rawText, '\r', true);
            String[] addresses = address == null?null:new String[]{address};
            return new AddressBookParsedResult(maybeWrap(name), pronunciation, phoneNumbers, emails, note, addresses, (String)null, (String)null, (String)null, (String)null);
        } else {
            return null;
        }
    }

    private static String[] matchMultipleValuePrefix(String prefix, int max, String rawText, boolean trim) {
        Vector values = null;

        for(int i = 1; i <= max; ++i) {
            String value = matchSinglePrefixedField(prefix + i + ':', rawText, '\r', trim);
            if(value == null) {
                break;
            }

            if(values == null) {
                values = new Vector(max);
            }

            values.addElement(value);
        }

        return values == null?null:toStringArray(values);
    }
}
