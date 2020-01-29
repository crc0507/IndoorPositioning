//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class CalendarParsedResult extends ParsedResult {
    private final String summary;
    private final String start;
    private final String end;
    private final String location;
    private final String attendee;
    private final String description;

    public CalendarParsedResult(String summary, String start, String end, String location, String attendee, String description) {
        super(ParsedResultType.CALENDAR);
        if(start == null) {
            throw new IllegalArgumentException();
        } else {
            validateDate(start);
            if(end == null) {
                end = start;
            } else {
                validateDate(end);
            }

            this.summary = summary;
            this.start = start;
            this.end = end;
            this.location = location;
            this.attendee = attendee;
            this.description = description;
        }
    }

    public String getSummary() {
        return this.summary;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

    public String getLocation() {
        return this.location;
    }

    public String getAttendee() {
        return this.attendee;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(100);
        maybeAppend(this.summary, result);
        maybeAppend(this.start, result);
        maybeAppend(this.end, result);
        maybeAppend(this.location, result);
        maybeAppend(this.attendee, result);
        maybeAppend(this.description, result);
        return result.toString();
    }

    private static void validateDate(String date) {
        if(date != null) {
            int length = date.length();
            if(length != 8 && length != 15 && length != 16) {
                throw new IllegalArgumentException();
            }

            int i;
            for(i = 0; i < 8; ++i) {
                if(!Character.isDigit(date.charAt(i))) {
                    throw new IllegalArgumentException();
                }
            }

            if(length > 8) {
                if(date.charAt(8) != 84) {
                    throw new IllegalArgumentException();
                }

                for(i = 9; i < 15; ++i) {
                    if(!Character.isDigit(date.charAt(i))) {
                        throw new IllegalArgumentException();
                    }
                }

                if(length == 16 && date.charAt(15) != 90) {
                    throw new IllegalArgumentException();
                }
            }
        }

    }
}
