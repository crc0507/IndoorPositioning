//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class SMSParsedResult extends ParsedResult {
    private final String[] numbers;
    private final String[] vias;
    private final String subject;
    private final String body;

    public SMSParsedResult(String number, String via, String subject, String body) {
        super(ParsedResultType.SMS);
        this.numbers = new String[]{number};
        this.vias = new String[]{via};
        this.subject = subject;
        this.body = body;
    }

    public SMSParsedResult(String[] numbers, String[] vias, String subject, String body) {
        super(ParsedResultType.SMS);
        this.numbers = numbers;
        this.vias = vias;
        this.subject = subject;
        this.body = body;
    }

    public String getSMSURI() {
        StringBuffer result = new StringBuffer();
        result.append("sms:");
        boolean first = true;

        for(int hasBody = 0; hasBody < this.numbers.length; ++hasBody) {
            if(first) {
                first = false;
            } else {
                result.append(',');
            }

            result.append(this.numbers[hasBody]);
            if(this.vias[hasBody] != null) {
                result.append(";via=");
                result.append(this.vias[hasBody]);
            }
        }

        boolean var5 = this.body != null;
        boolean hasSubject = this.subject != null;
        if(var5 || hasSubject) {
            result.append('?');
            if(var5) {
                result.append("body=");
                result.append(this.body);
            }

            if(hasSubject) {
                if(var5) {
                    result.append('&');
                }

                result.append("subject=");
                result.append(this.subject);
            }
        }

        return result.toString();
    }

    public String[] getNumbers() {
        return this.numbers;
    }

    public String[] getVias() {
        return this.vias;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBody() {
        return this.body;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(100);
        maybeAppend(this.numbers, result);
        maybeAppend(this.subject, result);
        maybeAppend(this.body, result);
        return result.toString();
    }
}
