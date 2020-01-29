//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class EmailAddressParsedResult extends ParsedResult {
    private final String emailAddress;
    private final String subject;
    private final String body;
    private final String mailtoURI;

    EmailAddressParsedResult(String emailAddress, String subject, String body, String mailtoURI) {
        super(ParsedResultType.EMAIL_ADDRESS);
        this.emailAddress = emailAddress;
        this.subject = subject;
        this.body = body;
        this.mailtoURI = mailtoURI;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBody() {
        return this.body;
    }

    public String getMailtoURI() {
        return this.mailtoURI;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(30);
        maybeAppend(this.emailAddress, result);
        maybeAppend(this.subject, result);
        maybeAppend(this.body, result);
        return result.toString();
    }
}
