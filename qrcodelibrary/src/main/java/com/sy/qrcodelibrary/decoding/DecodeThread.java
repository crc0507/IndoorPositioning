package com.sy.qrcodelibrary.decoding;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.sy.qrcodelibrary.ui.QRCodeActivity;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;


final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";
  private final QRCodeActivity activity;
  private final Hashtable<DecodeHintType, Object> hints;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;

  DecodeThread(QRCodeActivity activity,
               Vector<BarcodeFormat> decodeFormats,
               String characterSet,
               ResultPointCallback resultPointCallback) {

    this.activity = activity;
    handlerInitLatch = new CountDownLatch(1);

    hints = new Hashtable<>(3);

    if (decodeFormats == null || decodeFormats.isEmpty()) {
    	 decodeFormats = new Vector<>();
    	 decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
    	 decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
    	 decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
    }
    
    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
    }

    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(activity, hints);
    handlerInitLatch.countDown();
    Looper.loop();
  }

}
