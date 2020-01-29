//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.AbstractDoCoMoResultParser;
import com.google.zxing.client.result.AddressBookParsedResult;
import java.util.Vector;

final class BizcardResultParser extends AbstractDoCoMoResultParser {
    BizcardResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText != null && rawText.startsWith("BIZCARD:")) {
            String firstName = matchSingleDoCoMoPrefixedField("N:", rawText, true);
            String lastName = matchSingleDoCoMoPrefixedField("X:", rawText, true);
            String fullName = buildName(firstName, lastName);
            String title = matchSingleDoCoMoPrefixedField("T:", rawText, true);
            String org = matchSingleDoCoMoPrefixedField("C:", rawText, true);
            String[] addresses = matchDoCoMoPrefixedField("A:", rawText, true);
            String phoneNumber1 = matchSingleDoCoMoPrefixedField("B:", rawText, true);
            String phoneNumber2 = matchSingleDoCoMoPrefixedField("M:", rawText, true);
            String phoneNumber3 = matchSingleDoCoMoPrefixedField("F:", rawText, true);
            String email = matchSingleDoCoMoPrefixedField("E:", rawText, true);
            return new AddressBookParsedResult(maybeWrap(fullName), (String)null, buildPhoneNumbers(phoneNumber1, phoneNumber2, phoneNumber3), maybeWrap(email), (String)null, addresses, org, (String)null, title, (String)null);
        } else {
            return null;
        }
    }

    private static String[] buildPhoneNumbers(String number1, String number2, String number3) {
        Vector numbers = new Vector(3);
        if(number1 != null) {
            numbers.addElement(number1);
        }

        if(number2 != null) {
            numbers.addElement(number2);
        }

        if(number3 != null) {
            numbers.addElement(number3);
        }

        int size = numbers.size();
        if(size == 0) {
            return null;
        } else {
            String[] result = new String[size];

            for(int i = 0; i < size; ++i) {
                result[i] = (String)numbers.elementAt(i);
            }

            return result;
        }
    }

    private static String buildName(String firstName, String lastName) {
        return firstName == null?lastName:(lastName == null?firstName:firstName + ' ' + lastName);
    }
}
