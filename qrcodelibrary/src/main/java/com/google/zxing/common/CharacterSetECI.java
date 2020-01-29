//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.common.ECI;
import java.util.Hashtable;

public final class CharacterSetECI extends ECI {
    private static Hashtable VALUE_TO_ECI;
    private static Hashtable NAME_TO_ECI;
    private final String encodingName;

    private static void initialize() {
        VALUE_TO_ECI = new Hashtable(29);
        NAME_TO_ECI = new Hashtable(29);
        addCharacterSet(0, (String)"Cp437");
        addCharacterSet(1, (String[])(new String[]{"ISO8859_1", "ISO-8859-1"}));
        addCharacterSet(2, (String)"Cp437");
        addCharacterSet(3, (String[])(new String[]{"ISO8859_1", "ISO-8859-1"}));
        addCharacterSet(4, (String)"ISO8859_2");
        addCharacterSet(5, (String)"ISO8859_3");
        addCharacterSet(6, (String)"ISO8859_4");
        addCharacterSet(7, (String)"ISO8859_5");
        addCharacterSet(8, (String)"ISO8859_6");
        addCharacterSet(9, (String)"ISO8859_7");
        addCharacterSet(10, (String)"ISO8859_8");
        addCharacterSet(11, (String)"ISO8859_9");
        addCharacterSet(12, (String)"ISO8859_10");
        addCharacterSet(13, (String)"ISO8859_11");
        addCharacterSet(15, (String)"ISO8859_13");
        addCharacterSet(16, (String)"ISO8859_14");
        addCharacterSet(17, (String)"ISO8859_15");
        addCharacterSet(18, (String)"ISO8859_16");
        addCharacterSet(20, (String[])(new String[]{"SJIS", "Shift_JIS"}));
    }

    private CharacterSetECI(int value, String encodingName) {
        super(value);
        this.encodingName = encodingName;
    }

    public String getEncodingName() {
        return this.encodingName;
    }

    private static void addCharacterSet(int value, String encodingName) {
        CharacterSetECI eci = new CharacterSetECI(value, encodingName);
        VALUE_TO_ECI.put(new Integer(value), eci);
        NAME_TO_ECI.put(encodingName, eci);
    }

    private static void addCharacterSet(int value, String[] encodingNames) {
        CharacterSetECI eci = new CharacterSetECI(value, encodingNames[0]);
        VALUE_TO_ECI.put(new Integer(value), eci);

        for(int i = 0; i < encodingNames.length; ++i) {
            NAME_TO_ECI.put(encodingNames[i], eci);
        }

    }

    public static CharacterSetECI getCharacterSetECIByValue(int value) {
        if(VALUE_TO_ECI == null) {
            initialize();
        }

        if(value >= 0 && value < 900) {
            return (CharacterSetECI)VALUE_TO_ECI.get(new Integer(value));
        } else {
            throw new IllegalArgumentException("Bad ECI value: " + value);
        }
    }

    public static CharacterSetECI getCharacterSetECIByName(String name) {
        if(NAME_TO_ECI == null) {
            initialize();
        }

        return (CharacterSetECI)NAME_TO_ECI.get(name);
    }
}
