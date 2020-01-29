//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.VCardResultParser;

final class VEventResultParser extends ResultParser {
    private VEventResultParser() {
    }

    public static CalendarParsedResult parse(Result result) {
        String rawText = result.getText();
        if(rawText == null) {
            return null;
        } else {
            int vEventStart = rawText.indexOf("BEGIN:VEVENT");
            if(vEventStart < 0) {
                return null;
            } else {
                String summary = VCardResultParser.matchSingleVCardPrefixedField("SUMMARY", rawText, true);
                String start = VCardResultParser.matchSingleVCardPrefixedField("DTSTART", rawText, true);
                String end = VCardResultParser.matchSingleVCardPrefixedField("DTEND", rawText, true);
                String location = VCardResultParser.matchSingleVCardPrefixedField("LOCATION", rawText, true);
                String description = VCardResultParser.matchSingleVCardPrefixedField("DESCRIPTION", rawText, true);

                try {
                    return new CalendarParsedResult(summary, start, end, location, (String)null, description);
                } catch (IllegalArgumentException var9) {
                    return null;
                }
            }
        }
    }
}
