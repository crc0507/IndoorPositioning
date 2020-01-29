//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing;

import java.util.Hashtable;

public interface Reader {
  Result decode(BinaryBitmap var1) throws NotFoundException, ChecksumException, FormatException;

  Result decode(BinaryBitmap var1, Hashtable var2) throws NotFoundException, ChecksumException, FormatException;

  void reset();
}
