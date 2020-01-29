//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.client.result;

public final class AddressBookParsedResult extends ParsedResult {
    private final String[] names;
    private final String pronunciation;
    private final String[] phoneNumbers;
    private final String[] emails;
    private final String note;
    private final String[] addresses;
    private final String org;
    private final String birthday;
    private final String title;
    private final String url;

    public AddressBookParsedResult(String[] names, String pronunciation, String[] phoneNumbers, String[] emails, String note, String[] addresses, String org, String birthday, String title, String url) {
        super(ParsedResultType.ADDRESSBOOK);
        this.names = names;
        this.pronunciation = pronunciation;
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
        this.note = note;
        this.addresses = addresses;
        this.org = org;
        this.birthday = birthday;
        this.title = title;
        this.url = url;
    }

    public String[] getNames() {
        return this.names;
    }

    public String getPronunciation() {
        return this.pronunciation;
    }

    public String[] getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public String[] getEmails() {
        return this.emails;
    }

    public String getNote() {
        return this.note;
    }

    public String[] getAddresses() {
        return this.addresses;
    }

    public String getTitle() {
        return this.title;
    }

    public String getOrg() {
        return this.org;
    }

    public String getURL() {
        return this.url;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(100);
        maybeAppend(this.names, result);
        maybeAppend(this.pronunciation, result);
        maybeAppend(this.title, result);
        maybeAppend(this.org, result);
        maybeAppend(this.addresses, result);
        maybeAppend(this.phoneNumbers, result);
        maybeAppend(this.emails, result);
        maybeAppend(this.url, result);
        maybeAppend(this.birthday, result);
        maybeAppend(this.note, result);
        return result.toString();
    }
}
