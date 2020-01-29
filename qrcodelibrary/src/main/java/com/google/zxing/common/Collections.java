//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.common;

import com.google.zxing.common.Comparator;
import java.util.Vector;

public final class Collections {
    private Collections() {
    }

    public static void insertionSort(Vector vector, Comparator comparator) {
        int max = vector.size();

        for(int i = 1; i < max; ++i) {
            Object value = vector.elementAt(i);

            int j;
            Object valueB;
            for(j = i - 1; j >= 0 && comparator.compare(valueB = vector.elementAt(j), value) > 0; --j) {
                vector.setElementAt(valueB, j + 1);
            }

            vector.setElementAt(value, j + 1);
        }

    }
}
