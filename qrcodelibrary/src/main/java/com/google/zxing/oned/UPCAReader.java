//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;

import java.util.Hashtable;

public final class UPCAReader extends UPCEANReader {
    private final UPCEANReader ean13Reader = new EAN13Reader();

    public UPCAReader() {
    }

    public Result decodeRow(int rowNumber, BitArray row, int[] startGuardRange, Hashtable hints) throws NotFoundException, FormatException, ChecksumException {
        return maybeReturnResult(this.ean13Reader.decodeRow(rowNumber, row, startGuardRange, hints));
    }

    public Result decodeRow(int rowNumber, BitArray row, Hashtable hints) throws NotFoundException, FormatException, ChecksumException {
        return maybeReturnResult(this.ean13Reader.decodeRow(rowNumber, row, hints));
    }

    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return maybeReturnResult(this.ean13Reader.decode(image));
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, FormatException {
        return maybeReturnResult(this.ean13Reader.decode(image, hints));
    }

    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.UPC_A;
    }

    protected int decodeMiddle(BitArray row, int[] startRange, StringBuffer resultString) throws NotFoundException {
        return this.ean13Reader.decodeMiddle(row, startRange, resultString);
    }

    private static Result maybeReturnResult(Result result) throws FormatException {
        String text = result.getText();
        if(text.charAt(0) == 48) {
            return new Result(text.substring(1), (byte[])null, result.getResultPoints(), BarcodeFormat.UPC_A);
        } else {
            throw FormatException.getFormatInstance();
        }
    }
}
