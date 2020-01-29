//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing;

import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.google.zxing.pdf417.PDF417Reader;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;
import java.util.Vector;

public final class MultiFormatReader implements Reader {
    private Hashtable hints;
    private Vector readers;

    public MultiFormatReader() {
    }

    public Result decode(BinaryBitmap image) throws NotFoundException {
        this.setHints((Hashtable)null);
        return this.decodeInternal(image);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException {
        this.setHints(hints);
        return this.decodeInternal(image);
    }

    public Result decodeWithState(BinaryBitmap image) throws NotFoundException {
        if(this.readers == null) {
            this.setHints((Hashtable)null);
        }

        return this.decodeInternal(image);
    }

    public void setHints(Hashtable hints) {
        this.hints = hints;
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        Vector formats = hints == null?null:(Vector)hints.get(DecodeHintType.POSSIBLE_FORMATS);
        this.readers = new Vector();
        if(formats != null) {
            boolean addOneDReader = formats.contains(BarcodeFormat.UPC_A) || formats.contains(BarcodeFormat.UPC_E) || formats.contains(BarcodeFormat.EAN_13) || formats.contains(BarcodeFormat.EAN_8) || formats.contains(BarcodeFormat.CODE_39) || formats.contains(BarcodeFormat.CODE_93) || formats.contains(BarcodeFormat.CODE_128) || formats.contains(BarcodeFormat.ITF) || formats.contains(BarcodeFormat.RSS14) || formats.contains(BarcodeFormat.RSS_EXPANDED);
            if(addOneDReader && !tryHarder) {
                this.readers.addElement(new MultiFormatOneDReader(hints));
            }

            if(formats.contains(BarcodeFormat.QR_CODE)) {
                this.readers.addElement(new QRCodeReader());
            }

            if(formats.contains(BarcodeFormat.DATA_MATRIX)) {
                this.readers.addElement(new DataMatrixReader());
            }

            if(formats.contains(BarcodeFormat.PDF417)) {
                this.readers.addElement(new PDF417Reader());
            }

            if(addOneDReader && tryHarder) {
                this.readers.addElement(new MultiFormatOneDReader(hints));
            }
        }

        if(this.readers.isEmpty()) {
            if(!tryHarder) {
                this.readers.addElement(new MultiFormatOneDReader(hints));
            }

            this.readers.addElement(new QRCodeReader());
            this.readers.addElement(new DataMatrixReader());
            if(tryHarder) {
                this.readers.addElement(new MultiFormatOneDReader(hints));
            }
        }

    }

    public void reset() {
        int size = this.readers.size();

        for(int i = 0; i < size; ++i) {
            Reader reader = (Reader)this.readers.elementAt(i);
            reader.reset();
        }

    }

    private Result decodeInternal(BinaryBitmap image) throws NotFoundException {
        int size = this.readers.size();
        int i = 0;

        while(i < size) {
            Reader reader = (Reader)this.readers.elementAt(i);

            try {
                return reader.decode(image, this.hints);
            } catch (ReaderException var6) {
                ++i;
            }
        }

        throw NotFoundException.getNotFoundInstance();
    }
}
