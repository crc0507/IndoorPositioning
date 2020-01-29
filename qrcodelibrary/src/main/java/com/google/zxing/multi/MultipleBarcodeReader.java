//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import java.util.Hashtable;

public interface MultipleBarcodeReader {
    Result[] decodeMultiple(BinaryBitmap var1) throws NotFoundException;

    Result[] decodeMultiple(BinaryBitmap var1, Hashtable var2) throws NotFoundException;
}
