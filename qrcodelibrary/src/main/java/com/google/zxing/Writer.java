//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing;

import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public interface Writer {
  BitMatrix encode(String var1, BarcodeFormat var2, int var3, int var4) throws WriterException;

  BitMatrix encode(String var1, BarcodeFormat var2, int var3, int var4, Hashtable var5) throws WriterException;
}
